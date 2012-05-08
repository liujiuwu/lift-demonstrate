set SCRIPT_DIR=%~dp0

java -Dsbt.ivy.home="G:\java\repo\ivy2" -Xmx768M -jar "%SCRIPT_DIR%sbt-launch-0.11.2.jar" %*