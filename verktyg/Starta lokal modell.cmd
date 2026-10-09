@echo off
title Appfabriken - lokal modell (stang fonstret for att stoppa)
cd /d "%USERPROFILE%\appfabriken"
git pull -q --ff-only
start "Appfabriken panel" /min python verktyg\panel.py
powershell -NoProfile -ExecutionPolicy Bypass -File verktyg\kor-alla.ps1
echo.
echo Klart. Tryck en tangent for att stanga.
pause >nul
