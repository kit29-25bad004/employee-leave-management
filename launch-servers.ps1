$base = "C:\Users\Maya P\.gemini\antigravity\scratch\employee-leave-management"
$mysqld = "$base\database\mariadb\mariadb-10.11.7-winx64\bin\mysqld.exe"
$datadir = "$base\database\mariadb\data"
$startupBat = "$base\tomcat\apache-tomcat-10.1.20\bin\startup.bat"

Write-Output "1. Starting MySQL service..."
Start-Process -FilePath $mysqld -ArgumentList "--datadir=`"$datadir`"", "--port=3306" -WindowStyle Hidden

Write-Output "2. Starting Tomcat server..."
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.11"
$env:CATALINA_HOME = "$base\tomcat\apache-tomcat-10.1.20"

$psi = New-Object System.Diagnostics.ProcessStartInfo
$psi.FileName = "cmd.exe"
$psi.Arguments = "/c `"$startupBat`""
$psi.WindowStyle = [System.Diagnostics.ProcessWindowStyle]::Hidden
$psi.UseShellExecute = $true
$psi.EnvironmentVariables["JAVA_HOME"] = "C:\Program Files\Java\jdk-21.0.11"
$psi.EnvironmentVariables["CATALINA_HOME"] = "$base\tomcat\apache-tomcat-10.1.20"
[System.Diagnostics.Process]::Start($psi)

Write-Output "Waiting for Tomcat initialization (7 seconds)..."
Start-Sleep -Seconds 7

Write-Output "Testing HTTP connection..."
curl.exe -i http://localhost:8080/
