@ECHO OFF
setlocal enabledelayedexpansion
REM ----------------------------------------------------------------------------
REM Maven Wrapper startup script (Windows)
REM Baixa e usa a versao do Maven fixada em .mvn\wrapper\maven-wrapper.properties
REM ----------------------------------------------------------------------------

SET BASE_DIR=%~dp0
SET WRAPPER_DIR=%BASE_DIR%.mvn\wrapper
SET WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar
SET WRAPPER_PROPERTIES=%WRAPPER_DIR%\maven-wrapper.properties

IF NOT EXIST "%WRAPPER_JAR%" (
  FOR /F "tokens=1,2 delims==" %%A IN ('findstr "wrapperUrl" "%WRAPPER_PROPERTIES%"') DO SET WRAPPER_URL=%%B
  echo Baixando Maven Wrapper de: !WRAPPER_URL!
  powershell -Command "Invoke-WebRequest -Uri '!WRAPPER_URL!' -OutFile '%WRAPPER_JAR%'"
)

IF NOT "%JAVA_HOME%"=="" (
  SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
) ELSE (
  SET JAVA_EXE=java.exe
)

"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%BASE_DIR%" org.apache.maven.wrapper.MavenWrapperMain %*