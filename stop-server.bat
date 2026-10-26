@echo off
set "CATALINA_HOME=%~dp0tomcat\apache-tomcat-10.1.20"
set "DB_HOME=%~dp0database\mariadb\mariadb-10.11.7-winx64"

echo Stopping Apache Tomcat...
call "%CATALINA_HOME%\bin\shutdown.bat"

echo Stopping MySQL database...
"%DB_HOME%\bin\mysqladmin.exe" -u root -p"root" shutdown 2>nul
"%DB_HOME%\bin\mysqladmin.exe" -u root shutdown 2>nul
echo All services stopped.
