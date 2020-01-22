# Python program to fix headers and XML tags on qfx files intended for MS Money import 
# qfxfix@bpbco.net
# Usage:
# python cleanupQFX.py -i <inputFile> -o <outputFile>
#
from __future__ import print_function   # If code has to work in Python 2 and 3!

import sys, getopt, re

def fixFile(inputFile, outputFile):
  file = open(inputFile, "r")
  print ('Reading from', inputFile)
  contents = file.read()
  file.close()

  headerRecs = [
    "OFXHEADER:100",
    "DATA:OFXSGML",
    "VERSION:102",
    "SECURITY:NONE",
    "ENCODING:USASCII",
    "CHARSET:1252",
    "COMPRESSION:NONE",
    "OLDFILEUID:NONE",
    "NEWFILEUID:NONE",
    "",
    "<OFX>"]

  # Headers are anything that comes before the <OFX> tag
  # Replace all headers with Money-acceptable old style untagged headers for maximum compatibility with all Money versions
  # Remove extraneous elements from the OFX tag itself for compatibility e.g. '<OFX xmlns:ns2="http://ofx.net/types/2003/04">' --> '<OFX>' 
  # https://www.debuggex.com/cheatsheet/regex/python
  # http://xahlee.info/python/python_regex_flags.html
  # https://stackoverflow.com/questions/42581/python-re-sub-with-a-flag-does-not-replace-all-occurrences/7248027
  matchRegex  = r'^.+?<OFX.*?>'
  newStr      = '\n'.join(headerRecs)
  contents    = re.sub(matchRegex, newStr, contents, 1, re.DOTALL)
    
  # ensure CATEGORY tag followed by the word 'null' in tagged data instead of nothing at all
  # https://www.rexegg.com/regex-lookarounds.html
  matchRegex  = r'<CATEGORY>(?=[<\n\r])'
  newStr      = '<CATEGORY>null'
  contents    = re.sub(matchRegex, newStr, contents)

  # write output
  print ('Writing to', outputFile)
  file = open(outputFile, "w") 
  file.write(contents)
  file.close()

def usage():
  print ('cleanupQFX.py -i <inputFile> -o <outputFile>')

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
