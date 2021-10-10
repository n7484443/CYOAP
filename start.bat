@echo off
:Start

if exist %CD%\newImage\ (
 rmdir /s /q %CD%\image\
 move %CD%\newImage\image %CD%\image
 rmdir /s /q %CD%\newImage\
)

cd image\bin
cyoap.bat

