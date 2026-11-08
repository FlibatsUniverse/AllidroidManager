# Download LSPatch jar and convert it to an aar
# Jars cannot be used as a dependency without breaking R8 optimization

# LSPatch 1.2, the stable release. The October canary builds ship a native library and a loader that do not match.

LSPATCH_URL="https://github.com/JingMatrix/LSPatch/releases/download/v1.2/lspatch-v1.2-487-release.jar"
LSPATCH_FILE_NAME="lspatch"

function cleanup {
  rm -rf $LSPATCH_FILE_NAME.jar META-INF AndroidManifest.xml classes.jar R.txt
}

cleanup

# download lspatch
curl -sSL -o $LSPATCH_FILE_NAME.jar $LSPATCH_URL

# prepare aar contents
unzip -q $LSPATCH_FILE_NAME.jar "META-INF/**"
mv $LSPATCH_FILE_NAME.jar classes.jar
touch R.txt
cat >AndroidManifest.xml <<EOF
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="org.lsposed.lspatch">
    <uses-sdk android:minSdkVersion="28" android:targetSdkVersion="34" />
</manifest>
EOF

# the patcher copies these native libraries into every app it patches, so they must come from the same build as the jar
unzip -q -o $LSPATCH_FILE_NAME.jar "assets/lspatch/so/*" -d ../src/main

# add everything to aar
rm -f $LSPATCH_FILE_NAME.aar
zip -rq $LSPATCH_FILE_NAME.aar R.txt AndroidManifest.xml META-INF classes.jar
cleanup
