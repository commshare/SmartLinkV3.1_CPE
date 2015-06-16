versions="3.1.0"
if [ -n "$1" ]
then
	versions=$1
fi
android update project -p . -n SmartLinkV3.1 -t android-21
rm -rf ./gen
rm -rf ./bin
ant release
mv ./bin/SmartLinkV3.1-release.apk "./bin/SmartLinkV$versions.apk"


