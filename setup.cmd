@echo off
setlocal EnableDelayedExpansion

IF ! "[" "%?%" EQU "0" (
  echo "Please run with sudo privilege"
  exit "1"
)
SET _INTERPOLATION_0=
FOR /f "delims=" %%a in ('getent passwd $USER | cut -d: -f6') DO (SET "_INTERPOLATION_0=!_INTERPOLATION_0! %%a")
SET "USER_HOME_DIR=!_INTERPOLATION_0:~1!"
SET _INTERPOLATION_1=
FOR /f "delims=" %%a in ('getent passwd $USER | cut -d: -f6') DO (SET "_INTERPOLATION_1=!_INTERPOLATION_1! %%a")
SET "!_INTERPOLATION_1:~1!TEMP_DIR=!USER_HOME_DIR!/.tmpProject00001/"
echo "Downloading source to a temp directory: !TEMP_DIR! ..."
DEL /S "!TEMP_DIR!"
mkdir "-p" "!TEMP_DIR!"
cd "!TEMP_DIR!" || REM UNKNOWN: {"type":"Subshell","list":{"type":"CompoundList","commands":[{"type":"LogicalExpression","op":"and","left":{"type":"Command","name":{"text":"echo","type":"Word"},"suffix":[{"text":"Create temp directory failed. Setup exiting ...","type":"Word"}]},"right":{"type":"Command","name":{"text":"exit","type":"Word"}}}]}}
git "clone" "--depth" "1" "https://github.com/suyttthideptrai/MOEAFramework_tweak.git" "."
ls
echo "Compiling moeaframework-4.5.jar lib from source ..."
ant "build-maven"
cd "build"
mvn "clean" "package" "-Dmaven.test.skip=true"
SET "TARGET_DIR=build\target\moeaframework-4.5.jar"
SET "G_ID=org.moeaframework"
SET "A_ID=moeaframework"
SET "VER_NAME=4.5-CUSTOM"
IF "-f" "!TEMP_DIR!!TARGET_DIR!" (
  echo "Installing dependency to local maven repository"
  mvn "install:install-file" "-Dfile="!TEMP_DIR!!TARGET_DIR!"" "-DgroupId="!G_ID!"" "-DartifactId="!A_ID!"" "-Dversion="!VER_NAME!"" "-Dpackaging=jar"
  echo "Lib installation success!"
) ELSE (
  echo "File not found: !TEMP_DIR!!TARGET_DIR!"
  echo "list files inside build dir: "
  SET "VAR1=dist"
  ls "!TEMP_DIR!"
  ls "!TEMP_DIR!!VAR1!"
  echo "Lib installation failed!"
)
echo "Removing temp directory ..."
cd "~" || REM UNKNOWN: {"type":"Subshell","list":{"type":"CompoundList","commands":[{"type":"LogicalExpression","op":"and","left":{"type":"Command","name":{"text":"echo","type":"Word"},"suffix":[{"text":"\"Delete temp directory failed, you might need to remove $TEMP_DIR manually.\"","expansion":[{"loc":{"start":56,"end":64},"parameter":"TEMP_DIR","type":"ParameterExpansion"}],"type":"Word"}]},"right":{"type":"Command","name":{"text":"exit","type":"Word"}}}]}}
DEL /S "!TEMP_DIR!"