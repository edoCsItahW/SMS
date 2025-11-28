@echo off
REM build.bat

echo 修改执行策略以允许运行PowerShell脚本...
powershell -Command "Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process -Force"

echo 运行build.ps1...
powershell -File "%~dp0build.ps1"

pause