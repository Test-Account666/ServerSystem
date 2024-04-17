echo Checking system...
SYSTEM=$(command uname)

echo Checking for curl...
sleep 3
if ! command -v curl &> /dev/null
then
    echo "curl could not be found"
    if [ "$EUID" -ne 0 ]
  then echo "Please run as root for automatic installation"
  exit 1

  else
  if command -v apt &> /dev/null
  then
  apt install curl
  else
  if command -v pacman &> /dev/null
  then
  pacman -S curl
  else
  if command -v yum &> /dev/null
  then
  yum install curl
  else
  if command -v dnf &> /dev/null
  then
  dnf install curl
  if command -v zypper &> /dev/null
  then
  zypper install curl
  else
  echo "Could not automatically install curl, please install manually"
  exit 2
fi
fi
fi
fi
fi
fi
fi
echo Success!
echo Checking for git...
sleep 3
if ! command -v git &> /dev/null
then
    echo "git could not be found"
    if [ "$EUID" -ne 0 ]
  then echo "Please run as root for automatic installation"
  exit 1

  else
    if command -v apt &> /dev/null
  then
  apt install git
  else
  if command -v pacman &> /dev/null
  then
  pacman -S git
  else
  if command -v yum &> /dev/null
  then
  yum install git
  else
  if command -v dnf &> /dev/null
  then
  dnf install git
  if command -v zypper &> /dev/null
  then
  zypper install git
  else
  echo "Could not automatically install git, please install manually"
  exit 2
fi
fi
fi
fi
fi
fi
fi
echo Success!
echo Checking for unzip...
sleep 3
if ! command -v unzip &> /dev/null
then
    echo "unzip could not be found"
    if [ "$EUID" -ne 0 ]
  then echo "Please run as root for automatic installation"
  exit 1

  else
    if command -v apt &> /dev/null
  then
  apt install unzip
  else
  if command -v pacman &> /dev/null
  then
  pacman -S unzip
  else
  if command -v yum &> /dev/null
  then
  yum install unzip
  else
  if command -v dnf &> /dev/null
  then
  dnf install unzip
  if command -v zypper &> /dev/null
  then
  zypper install unzip
  else
  echo "Could not automatically install unzip, please install manually"
  exit 2
fi
fi
fi
fi
fi
fi
fi
echo Success!
echo Starting ServerSystem project setup...
sleep 3
mkdir libs
mkdir work
cd work || exit
echo Success!
echo Downloading BuildTools...
sleep 3
curl -O https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
echo Success!
mkdir 1.17
cp BuildTools.jar 1.17/BuildTools.jar
mkdir 1.18.2
cp BuildTools.jar 1.18.2/BuildTools.jar
mkdir 1.19
cp BuildTools.jar 1.19/BuildTools.jar
mkdir 1.20
cp BuildTools.jar 1.19/BuildTools.jar

mkdir java17
cd java17 || exit
echo Downloading Java-17 ...
sleep 3

if [[ "$SYSTEM" == *"MINGW64_NT"* ]];
then
PATH_JAVA_17="jdk-17_windows-x64_bin"
curl -O https://download.oracle.com/java/17/latest/$PATH_JAVA_17.zip
else
PATH_JAVA_17="jdk-17_linux-x64_bin"
curl -O https://download.oracle.com/java/17/latest/$PATH_JAVA_17.tar.gz
fi

echo Success!
echo Extracting Java-17...
sleep 3

if [[ "$SYSTEM" == *"MINGW64_NT"* ]];
then
unzip -o $PATH_JAVA_17.zip
PATH_JAVA_17="jdk-17.0.5"
export PATH="$(pwd)/$PATH_JAVA_17/bin:$PATH"
export JAVA_HOME="$(pwd)/$PATH_JAVA_17/"
else
tar -xvf $PATH_JAVA_17.tar.gz
PATH_JAVA_17="jdk-17.0.5"
fi

echo Success!
cd .. || exit

mkdir java16
cd java16 || exit
echo Downloading Java-16 ...
sleep 3

if [[ "$SYSTEM" == *"MINGW64_NT"* ]];
then
PATH_JAVA_16="openjdk-16.0.2_windows-x64_bin"
curl -O https://download.java.net/java/GA/jdk16.0.2/d4a915d82b4c4fbb9bde534da945d746/7/GPL/$PATH_JAVA_16.zip
else
PATH_JAVA_16="openjdk-16.0.2_linux-x64_bin"
curl -O https://download.java.net/java/GA/jdk16.0.2/d4a915d82b4c4fbb9bde534da945d746/7/GPL/$PATH_JAVA_16.tar.gz
fi

echo Success!
echo Extracting Java-16...
sleep 3

if [[ "$SYSTEM" == *"MINGW64_NT"* ]];
then
unzip -o $PATH_JAVA_16.zip
PATH_JAVA_16="jdk-16.0.2"
export PATH="$(pwd)/$PATH_JAVA_16/bin:$PATH"
export JAVA_HOME="$(pwd)/$PATH_JAVA_16/"
else
tar -xvf $PATH_JAVA_16.tar.gz
PATH_JAVA_16="jdk-16.0.2"
fi

echo Success!
cd .. || exit
cd 1.17 || exit
echo Building Spigot 1.17...
sleep 3
./../java16/$PATH_JAVA_16/bin/java -jar BuildTools.jar --rev 1.17 --compile CRAFTBUKKIT,SPIGOT
echo Success!
cd .. || exit
cp 1.17/spigot-1.17.jar ../libs/spigot-1.17.jar
cd 1.18.2 || exit
echo Building Spigot 1.18.2...
sleep 3
./../java17/$PATH_JAVA_17/bin/java -jar BuildTools.jar --rev 1.18.2 --compile CRAFTBUKKIT,SPIGOT
echo Success!
cd .. || exit
cp 1.18.2/Spigot/Spigot-Server/target/spigot-1.18.2-R0.1-SNAPSHOT.jar ../libs/spigot-1.18.2.jar
cd 1.19 || exit
echo Building Spigot 1.19...
sleep 3
./../java17/$PATH_JAVA_17/bin/java -jar BuildTools.jar --rev 1.19 --compile CRAFTBUKKIT,SPIGOT
echo Success!
cd .. || exit
cp 1.19/Spigot/Spigot-Server/target/spigot-1.19-R0.1-SNAPSHOT.jar ../libs/spigot-1.19.jar

cd .. || exit
echo Spigot dependencies complete!
echo Downloading PlotSquared...
echo 18.12.12-be48507-2053...
sleep 3
cd work || exit
mkdir PlotSquaredv18
cd PlotSquaredv18 || exit
curl -o PlotSquared-Bukkit-18.12.12-be48507-2053.jar -L https://dev.bukkit.org/projects/plotsquared/files/2647923/download
cp PlotSquared-Bukkit-18.12.12-be48507-2053.jar ../../libs/PlotSquared-Bukkit-18.12.12-be48507-2053.jar
cd .. || exit
echo 4.4.495...
sleep 3
mkdir PlotSquaredv4
cd PlotSquaredv4 || exit
curl -o PlotSquared-Bukkit-4.494.jar -L https://dev.bukkit.org/projects/plotsquared/files/2932066/download
cp PlotSquared-Bukkit-4.494.jar ../../libs/PlotSquared-Bukkit-4.494.jar
cd .. || exit
echo Compiling PlotSquaredv6...
sleep 3
mkdir PlotSquaredv6
cd PlotSquaredv6 || exit
git clone https://github.com/IntellectualSites/PlotSquared.git
cd PlotSquared || exit
./gradlew shadowJar
cp Bukkit/build/libs/PlotSquared-Bukkit*-SNAPSHOT.jar ../../../libs/PlotSquared-Bukkit-6.0.6-SNAPSHOT.jar
echo Success!
sleep 3
echo Downloading LuckPerms...
sleep 3
cd .. || exit
cd .. || exit
mkdir LuckPerms
cd LuckPerms || exit
curl -o luckperms.zip -L https://ci.lucko.me/job/LuckPerms/lastSuccessfulBuild/artifact/bukkit/loader/build/libs/*/*zip*/libs.zip
unzip luckperms.zip
cp LuckPerms-Bukkit* ../../libs/LuckPerms-Bukkit-5.4.54.jar
cd .. || exit
echo Success!
sleep 3
echo Finished project setup!
sleep 10
