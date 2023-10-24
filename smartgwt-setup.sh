#!/bin/bash

echo "Downloading SmartGWT Framework"
mvn isc:download -DbuildNumber=13.0p -Dlicense=ENTERPRISE -Dproduct=SMARTGWT -DincludeMessaging=true -DincludeAnalytics=true -Dworkdir=.

# Get the build number
BUILD_VERSION=$(basename ./SmartGWT/Enterprise/13.0p/*/)

echo "SmartGWT build version: $BUILD_VERSION"

if [ ! -d "transformer" ]; then
  echo "Downloading Eclipse Transformer"
  mkdir -p transformer/libs
  curl -s https://repo1.maven.org/maven2/org/eclipse/transformer/org.eclipse.transformer.cli/0.5.0/org.eclipse.transformer.cli-0.5.0.jar --output ./transformer/org.eclipse.transformer.cli-0.5.0.jar
  curl -s https://repo1.maven.org/maven2/org/eclipse/transformer/org.eclipse.transformer/0.5.0/org.eclipse.transformer-0.5.0.jar --output ./transformer/libs/org.eclipse.transformer-0.5.0.jar
  curl -s https://repo1.maven.org/maven2/org/eclipse/transformer/org.eclipse.transformer.jakarta/0.5.0/org.eclipse.transformer.jakarta-0.5.0.jar --output ./transformer/libs/org.eclipse.transformer.jakarta-0.5.0.jar
  curl -s https://repo1.maven.org/maven2/commons-cli/commons-cli/1.5.0/commons-cli-1.5.0.jar --output ./transformer/libs/commons-cli-1.5.0.jar
  curl -s https://repo1.maven.org/maven2/biz/aQute/bnd/biz.aQute.bnd.transform/6.3.1/biz.aQute.bnd.transform-6.3.1.jar --output ./transformer/libs/biz.aQute.bnd.transform-6.3.1.jar
  curl -s https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar --output ./transformer/libs/slf4j-api-1.7.36.jar
  curl -s https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar --output ./transformer/libs/slf4j-simple-1.7.36.jar 
else
  echo "transformer directory exists, skipping download"
fi

echo "Running transformer"
java -jar ./transformer/org.eclipse.transformer.cli-0.5.0.jar ./SmartGWT/Enterprise/13.0p/$BUILD_VERSION/lib/
cp -r ./SmartGWT/Enterprise/13.0p/$BUILD_VERSION/output_lib/* ./SmartGWT/Enterprise/13.0p/$BUILD_VERSION/lib/
rm -r ./SmartGWT/Enterprise/13.0p/$BUILD_VERSION/output_lib/

echo "Installing to repository"
mvn isc:deploy -DbuildNumber=13.0p -Dlicense=ENTERPRISE -DrepositoryUrl=[REPOSITORY URL] -DrepositoryId=[REPOSITORY ID] -Dproduct=SMARTGWT -DincludeMessaging=true -DincludeAnalytics=true -DskipDownload=true -DskipExtraction=true -Dworkdir=. -DbuildDate=$BUILD_VERSION
