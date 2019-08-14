@echo off
rem set these variable to the locations and files needed
set pythonExe=c:\program files\python37\python.exe
set pythonFilePath=y:\documents\application data\ms money\tools
set outputPath=y:\documents\application data\ms money\tools\cleanup
set mnyImportExe=c:\program files (x86)\microsoft money 2005\mnycorefiles\mnyimprt.exe

rem Main actions
set inputFileAndPath=%~f1%
set inputFileStem=%~n1%
cd /d %pythonFilePath%
"%pythonExe%" cleanupQFX.py -i "%inputFileAndPath%" -o "%outputPath%\%inputFileStem%-fixed.ofx"
cd /d %outputPath%
"%mnyImportExe%" %inputFileStem%-fixed.ofx

rem pause


