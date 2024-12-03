#require sudo privilege
if ! [ $? -eq 0 ]; then
  echo "Please run with sudo privilege"
  exit 1
fi

#Get user home dir
USER_HOME_DIR=$(getent passwd $USER | cut -d: -f6)
TEMP_DIR="$USER_HOME_DIR/.tmpProject00001/"

#create tmp directory for lib installation
echo "Downloading source to a temp directory: $TEMP_DIR ..."
rm -rf "$TEMP_DIR"
mkdir -p "$TEMP_DIR"
cd "$TEMP_DIR" || (echo "Create temp directory failed. Setup exiting ..." && exit)
git clone --depth 1 https://github.com/suyttthideptrai/MOEAFramework_tweak.git .
ls

#build from source, install mvn dependency locally
echo "Compiling moeaframework-4.5.jar lib from source ..."
ant build-maven
cd build || (echo "Build failed. Setup exiting ..." && exit)
mvn clean package -Dmaven.test.skip=true

TARGET_DIR="build/target/moeaframework-4.5.jar"
G_ID="org.moeaframework"
A_ID="moeaframework"
VER_NAME="4.5-CUSTOM"

if [ -f "$TEMP_DIR$TARGET_DIR" ]; then
  echo "Installing dependency to local maven repository"
  mvn install:install-file \
      -Dfile="$TEMP_DIR$TARGET_DIR" \
      -DgroupId="$G_ID" \
      -DartifactId="$A_ID" \
      -Dversion="$VER_NAME" \
      -Dpackaging=jar
  echo "Lib installation success!"
else
  echo "File not found: $TEMP_DIR$TARGET_DIR"
  echo "list files inside build dir: "
  VAR1="dist"
  ls "$TEMP_DIR"
  ls "$TEMP_DIR$VAR1"
  echo "Lib installation failed!"
fi


#remove tmp dir
echo "Removing temp directory ..."
cd ~ || (echo "Delete temp directory failed, you might need to remove $TEMP_DIR manually." && exit)
rm -rf "$TEMP_DIR"
