Suggested usage of python script and Windows batch file for Microsoft Money
1. Associate QFX files with downloaded batch file cleanupQfxThenImport.bat which you will need to place in a preferred directory
   - I use http://defaultprogramseditor.com/ to manage these associations
   - Here is the command line I used for the association: 
     "y:\documents\application data\ms money\tools\cleanupQfxThenImport.bat" "%1"
   - You can also associate QFX with the icon file for MS Money so it the QFX files look right
2. Download Python to run on your PC
   - https://www.python.org/downloads/
   - place python script cleanupQFX.py in a preferred location
3.  Edit the batch file so the directories and file names match to those you are using

The outcome is that Wells Fargo QFX downloads import fine as do Amex ones (currently broken). All other QFX files I have tested continue to work as before.
Also because this process creates an intermediate OFX file for upload, it removes the need to do a registry hack to get MS Money to recognize QFX files.
   
Apologies if I missed anything in github etiquette - I'm new to using it.
Thanks to Hung Lee for the original python script!