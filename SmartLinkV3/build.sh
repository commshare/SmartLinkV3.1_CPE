android update project -p . -n SmartLinkV3 -t android-16
rm -rf ./gen
rm -rf ./bin
ant release
mv ./bin/SmartLinkV3-release.apk "./bin/SmartLinkV3.apk"


