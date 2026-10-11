@echo off
cd /d "%USERPROFILE%\appfabriken"
git fetch -q origin main
git diff --quiet && git diff --cached --quiet && git checkout -q main && git merge -q --ff-only origin/main 2>nul
start "Appfabriken panel" /min python verktyg\panel.py
