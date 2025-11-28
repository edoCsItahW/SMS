@echo off
REM run.bat

echo 修改执行策略以允许运行PowerShell脚本...
powershell -Command "Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process -Force"

echo 运行run.ps1...
powershell -File "%~dp0run.ps1"

pause