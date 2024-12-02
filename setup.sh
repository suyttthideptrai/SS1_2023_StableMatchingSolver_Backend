#prerequisite
sudo apt upgrade
sudo apt update
sudo apt -y install maven
sudo apt -y install git

TEMP_DIR="$HOME/tmpProject/"

#create tmp directory for lib installation
sudo mkdir TEMP_DIR
cd TEMP_DIR || echo "Create temp directory failed. Setup exiting ..." && exit
sudo git clone https://github.com/suyttthideptrai/MOEAFramework_tweak.git .

#build from source, install mvn dependency locally
sudo mvn compile
sudo mvn install:install-file \
    -Dfile=/dist/MOEAFramework-4.5.jar \
    -DgroupId=org.moeaframework \
    -DartifactId=moeaframework \
    -Dversion=4.5 \
    -Dpackaging=jar

#remove tmp dir
cd ~ || echo "Delete temp directory failed, you might need to remove $TEMP_DIR manually." && exit
sudo rm -rf TEMP_DIR
