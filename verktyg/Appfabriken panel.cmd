@echo off
rem Panelen kors från en egen kopia i %LOCALAPPDATA%\appfabriken, hamtad från main. Da spelar det
rem ingen roll vilken gren eller vilka andringar repots arbetskopia har (den lokala modellen byter gren).
set PD=%LOCALAPPDATA%\appfabriken
if not exist "%PD%" mkdir "%PD%"
cd /d "%USERPROFILE%\appfabriken"
git fetch -q origin main
git show origin/main:verktyg/panel.py > "%PD%\panel.py"
rem Stang en gammal panel som fortfarande lyssnar pa porten
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":8765 " ^| findstr LISTENING') do taskkill /f /pid %%p >nul 2>nul
start "Appfabriken panel" /min python "%PD%\panel.py"
