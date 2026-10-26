@echo off
title ELMS Server Launcher
cd /d "%~dp0"

set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.11"
set "CATALINA_HOME=%~dp0tomcat\apache-tomcat-10.1.20"
set "DB_HOME=%~dp0database\mariadb\mariadb-10.11.7-winx64"

echo ======================================================================
echo  Employee Leave Management System (ELMS) - Server Launcher
echo ======================================================================

echo [1/2] Starting MySQL Database...
start "MySQL Database Server" /min "%DB_HOME%\bin\mysqld.exe" --datadir="%~dp0database\mariadb\data" --port=3306

timeout /t 2 /nobreak >nul

echo [2/2] Starting Tomcat Web Server...
start "Tomcat Web Server" /min "%CATALINA_HOME%\bin\catalina.bat" run

timeout /t 6 /nobreak >nul

echo.
echo Application is online and ready!
echo Opening browser at http://localhost:8080/
start http://localhost:8080/

pause
