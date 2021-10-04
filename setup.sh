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

echo Starting ServerSystem project setup...
echo Cloning ServerSystem git...
sleep 3
git clone https://github.com/TheBlackEntity/ServerSystem.git
cd ServerSystem
mkdir libs
mkdir work
cd work
echo Success!
echo Downloading BuildTools...
sleep 3
curl -O https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
echo Success!
mkdir 1.8.8
cp BuildTools.jar 1.8.8/BuildTools.jar
mkdir 1.9
cp BuildTools.jar 1.9/BuildTools.jar
mkdir 1.10.2
cp BuildTools.jar 1.10.2/BuildTools.jar
mkdir 1.11.1
cp BuildTools.jar 1.11.1/BuildTools.jar
mkdir 1.12.2
cp BuildTools.jar 1.12.2/BuildTools.jar
mkdir 1.13
cp BuildTools.jar 1.13/BuildTools.jar
mkdir 1.13.2
cp BuildTools.jar 1.13.2/BuildTools.jar
mkdir 1.14.4
cp BuildTools.jar 1.14.4/BuildTools.jar
mkdir 1.15.2
cp BuildTools.jar 1.15.2/BuildTools.jar
mkdir 1.16.1
cp BuildTools.jar 1.16.1/BuildTools.jar
mkdir 1.16.3
cp BuildTools.jar 1.16.3/BuildTools.jar
mkdir 1.17
cp BuildTools.jar 1.17/BuildTools.jar
mkdir java16
cd java16
echo Downloading Java16...
sleep 3
curl -O https://download.java.net/java/GA/jdk16.0.2/d4a915d82b4c4fbb9bde534da945d746/7/GPL/openjdk-16.0.2_linux-x64_bin.tar.gz
echo Success!
echo Extracting Java16...
sleep 3
tar -xvf openjdk-16.0.2_linux-x64_bin.tar.gz
echo Success!
cd ..
mkdir java8
cd java8
echo Downloading Java8...
sleep 3
curl -O https://cdn.azul.com/zulu/bin/zulu8.56.0.21-ca-jdk8.0.302-linux_x64.tar.gz
echo Success!
echo Extracting Java8...
sleep 3
tar -xvf zulu8.56.0.21-ca-jdk8.0.302-linux_x64.tar.gz
echo Success!
cd ..
cd 1.8.8
echo Building Spigot 1.8.8...
sleep 3
./../java8/zulu8.56.0.21-ca-jdk8.0.302-linux_x64/bin/java -jar BuildTools.jar --rev 1.8.8
echo Success!
cd ..
cp 1.8.8/spigot-1.8.8.jar ../libs/spigot-1.8.8.jar
cd 1.9
echo Building Spigot 1.9...
sleep 3
./../java8/zulu8.56.0.21-ca-jdk8.0.302-linux_x64/bin/java -jar BuildTools.jar --rev 1.9
echo Success!
cd ..
cp 1.9/spigot-1.9.jar ../libs/spigot-1.9.jar
cd 1.10.2
echo Building Spigot 1.10.2...
sleep 3
./../java8/zulu8.56.0.21-ca-jdk8.0.302-linux_x64/bin/java -jar BuildTools.jar --rev 1.10.2
echo Success!
cd ..
cp 1.10.2/spigot-1.10.2.jar ../libs/spigot-1.10.2.jar
cd 1.11.1
echo Building Spigot 1.11.1...
sleep 3
./../java8/zulu8.56.0.21-ca-jdk8.0.302-linux_x64/bin/java -jar BuildTools.jar --rev 1.11.1
echo Success!
cd ..
cp 1.11.1/spigot-1.11.2.jar ../libs/spigot-1.11.1.jar
cd 1.12.2
echo Building Spigot 1.12.2...
sleep 3
./../java8/zulu8.56.0.21-ca-jdk8.0.302-linux_x64/bin/java -jar BuildTools.jar --rev 1.12.2
echo Success!
cd ..
cp 1.12.2/spigot-1.12.2.jar ../libs/spigot-1.12.2.jar
cd 1.13
echo Building Spigot 1.13...
sleep 3
./../java8/zulu8.56.0.21-ca-jdk8.0.302-linux_x64/bin/java -jar BuildTools.jar --rev 1.13
echo Success!
cd ..
cp 1.13/spigot-1.13.jar ../libs/spigot-1.13.jar
cd 1.13.2
echo Building Spigot 1.13.2...
sleep 3
./../java8/zulu8.56.0.21-ca-jdk8.0.302-linux_x64/bin/java -jar BuildTools.jar --rev 1.13.2
echo Success!
cd ..
cp 1.13.2/spigot-1.13.2.jar ../libs/spigot-1.13.2.jar
cd 1.14.4
echo Building Spigot 1.14.4...
sleep 3
./../java8/zulu8.56.0.21-ca-jdk8.0.302-linux_x64/bin/java -jar BuildTools.jar --rev 1.14.4
echo Success!
cd ..
cp 1.14.4/spigot-1.14.4.jar ../libs/spigot-1.14.4.jar
cd 1.15.2
echo Building Spigot 1.15.2...
sleep 3
./../java8/zulu8.56.0.21-ca-jdk8.0.302-linux_x64/bin/java -jar BuildTools.jar --rev 1.15.2
echo Success!
cd ..
cp 1.15.2/spigot-1.15.2.jar ../libs/spigot-1.15.2.jar
cd 1.16.1
echo Building Spigot 1.16.1...
sleep 3
./../java8/zulu8.56.0.21-ca-jdk8.0.302-linux_x64/bin/java -jar BuildTools.jar --rev 1.16.1
echo Success!
cd ..
cp 1.16.1/spigot-1.16.1.jar ../libs/spigot-1.16.1.jar
cd 1.16.3
echo Building Spigot 1.16.3...
sleep 3
./../java8/zulu8.56.0.21-ca-jdk8.0.302-linux_x64/bin/java -jar BuildTools.jar --rev 1.16.3
echo Success!
cd ..
cp 1.16.3/spigot-1.16.3.jar ../libs/spigot-1.16.3.jar
cd 1.17
echo Building Spigot 1.17...
sleep 3
./../java16/jdk-16.0.2/bin/java -jar BuildTools.jar --rev 1.17
echo Success!
cd ..
cp 1.17/spigot-1.17.jar ../libs/spigot-1.17.jar
cd ..
echo Spigot dependencies complete!
echo Downloading PlotSquared...
echo 18.12.12-be48507-2053...
sleep 3
cd work
mkdir PlotSquaredv18
cd PlotSquaredv18
curl -o PlotSquared-Bukkit-18.12.12-be48507-2053.jar -L https://dev.bukkit.org/projects/plotsquared/files/2647923/download
cp PlotSquared-Bukkit-18.12.12-be48507-2053.jar ../../libs/PlotSquared-Bukkit-18.12.12-be48507-2053.jar
cd ..
echo 4.4.495...
sleep 3
mkdir PlotSquaredv4
cd PlotSquaredv4
curl -o PlotSquared-Bukkit-4.494.jar -L https://dev.bukkit.org/projects/plotsquared/files/2932066/download
cp PlotSquared-Bukkit-4.494.jar ../../libs/PlotSquared-Bukkit-4.494.jar
cd ..
echo Compiling PlotSquaredv6...
sleep 3
mkdir PlotSquaredv6
cd PlotSquaredv6
git clone https://github.com/IntellectualSites/PlotSquared.git
cd PlotSquared
./gradlew build
cp build/libs/PlotSquared-Bukkit* ../../../libs/PlotSquared-Bukkit-6.0.6-SNAPSHOT.jar
echo Success!
sleep 3
echo Finished project setup!
sleep 10
