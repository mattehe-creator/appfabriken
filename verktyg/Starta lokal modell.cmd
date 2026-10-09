@echo off
title Appfabriken - lokal modell (stang fonstret for att stoppa)
cd /d "%USERPROFILE%\appfabriken"
rem Hamta senaste main. Har lokal main egna commits sparas de pa sparad/lokal-main och main satts till origin/main.
git fetch -q origin main
git diff --quiet && git diff --cached --quiet && git checkout -q main && (git merge -q --ff-only origin/main 2>nul || (git branch -f sparad/lokal-main main && git reset -q --hard origin/main && echo Lokal main hade egna commits. Sparade pa sparad/lokal-main.))
start "Appfabriken panel" /min python verktyg\panel.py
powershell -NoProfile -ExecutionPolicy Bypass -File verktyg\kor-alla.ps1
echo.
echo Klart. Tryck en tangent for att stanga.
pause >nul
