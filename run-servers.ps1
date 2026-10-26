$ErrorActionPreference = "SilentlyContinue"

$baseDir = "C:\Users\Maya P\.gemini\antigravity\scratch\employee-leave-management"
$dbExe = "$baseDir\database\mariadb\mariadb-10.11.7-winx64\bin\mysqld.exe"
$dataDir = "$baseDir\database\mariadb\data"
$tomcatDir = "$baseDir\tomcat\apache-tomcat-10.1.20"

Write-Host "Checking MySQL..."
$mysqlRunning = Get-NetTCPConnection -LocalPort 3306 -ErrorAction SilentlyContinue
if (-not $mysqlRunning) {
    Write-Host "Starting MySQL on port 3306..."
    Start-Process -FilePath $dbExe -ArgumentList "--datadir=`"$dataDir`"", "--port=3306" -WindowStyle Hidden
    Start-Sleep -Seconds 3
}

Write-Host "Checking Tomcat..."
$tomcatRunning = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if (-not $tomcatRunning) {
    Write-Host "Starting Tomcat on port 8080..."
    $env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.11"
    $env:CATALINA_HOME = $tomcatDir
    $startupBat = "$tomcatDir\bin\startup.bat"
    Start-Process -FilePath "cmd.exe" -ArgumentList "/c `"$startupBat`"" -WindowStyle Hidden
    Start-Sleep -Seconds 5
}

$dbStatus = Get-NetTCPConnection -LocalPort 3306 -ErrorAction SilentlyContinue
$webStatus = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue

Write-Host "MySQL Port 3306:" ($dbStatus.State)
Write-Host "Tomcat Port 8080:" ($webStatus.State)
