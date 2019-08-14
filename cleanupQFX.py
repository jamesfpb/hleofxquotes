# Python program to fix up missing end of line 
# Orginal version: hleofxquotes@gmail.com
# Revised version: qfxfix@bpbco.net
# Revisions:
# 1. Now won't add line breaks if file already formatted correctly
# 2. Fixes <OFX> tags for compatibility with Microsoft Money
# 3. Removes tagging errors found historically for Chase and Amex
#
#
# Usage:
# python cleanupQFX.py  -i Checking1.qfx -o out.qfx
#
from __future__ import print_function   # If code has to work in Python 2 and 3!

import sys, getopt, re

def fixFile(inputFile, outputFile):
  file = open(inputFile, "r")
  print ('Reading from', inputFile)
  header     = True
  prevChar   = ''

  headerList = []
  bodyList   = []

  # read intput and split it into two lists: header and body
  while 1:
    char = file.read(1)
    if not char: break

    if header:
      if char == '<':
        header = False
        bodyList.append(char)
      else:
        headerList.append(char)
    else:
      if char == '<' and not prevChar == '\n':
        bodyList.append('\n')
      bodyList.append(char)

    prevChar = char

  file.close()

  # fix up the header list for older versions of OFX standard
  headerKeys = [
    "DATA:",
    "VERSION:",
    "SECURITY:",
    "ENCODING:",
    "CHARSET:",
    "COMPRESSION:",
    "OLDFILEUID:",
    "NEWFILEUID:",
  ]
  fixedHeaders = "".join(headerList)
  for headerKey in headerKeys:
    matchRegex   = r'(?<![\n])' + headerKey
    newStr       = '\n' + headerKey
    fixedHeaders = re.sub(matchRegex, newStr, fixedHeaders)

  fixedBody = "".join(bodyList)
  matchRegex = r'<OFX.+>'
  newStr     = '<OFX>'
  fixedBody  = re.sub(matchRegex, newStr, fixedBody)
  matchRegex = r'<CATEGORY>(?=[\n\r])'
  newStr     = '<CATEGORY>null'
  fixedBody  = re.sub(matchRegex, newStr, fixedBody)
#  need to change e.g. '<OFX xmlns:ns2="http://ofx.net/types/2003/04">' to '<OFX>'
  matchRegex = r'<OFX.+>'
  newStr     = '<OFX>'
  fixedBody  = re.sub(matchRegex, newStr, fixedBody)
  

  # write output
  print ('Writing to', outputFile)
  file = open(outputFile, "w")
  if fixedHeaders:
    file.write(fixedHeaders)
    file.write("\n")
    file.write("\n")
  file.write(fixedBody)
  file.write("\n")
  file.close()

def usage():
  print ('fixWellsFargo.py -i <inputFile> -o <outputFile>')

def main(argv):
  inputFile = ''
  outputFile = ''

  try:
    opts, args = getopt.getopt(argv,"hi:o:",["ifile=","ofile="])
  except getopt.GetoptError:
    usage()
    sys.exit(2)
  for opt, arg in opts:
    if opt == '-h':
      usage()
      sys.exit()
    elif opt in ("-i", "--ifile"):
      inputFile = arg
    elif opt in ("-o", "--ofile"):
      outputFile = arg

  if not inputFile:
    usage()
    sys.exit(2)
  if not outputFile:
    usage()
    sys.exit(2)

  fixFile(inputFile, outputFile)

if __name__ == "__main__":
  main(sys.argv[1:])

