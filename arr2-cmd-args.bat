@echo off

set CD=%~dp0

set JAVA_HOME=C:\Program Files\Java\jdk-23

set JAVA=java
if not "%JAVA_HOME%"=="" (
  set JAVA="%JAVA_HOME%\bin\%JAVA%"
)

set CP=%CD%target\classes

%JAVA% -cp "%CP%" SimpleArchiver.Main %*
