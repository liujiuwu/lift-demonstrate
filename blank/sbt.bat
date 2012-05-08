set SCRIPT_DIR=%~dp0
java -XX:MaxPermSize=512m -Xmx768M -jar "%SCRIPT_DIR%sbt-launch-0.11.2.jar" %*
