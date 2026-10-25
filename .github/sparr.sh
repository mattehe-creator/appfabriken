#!/usr/bin/env bash
# Spärren: mekanisk kontroll av en lokal PR innan någon AI granskar den.
# Anropas av cursor-granskning.yml med grenen checkad ut (fetch-depth 0).
# Skriver granskning/<uppgift>.md när den kan avgöra själv:
#   - underkänd, med försök 2, när en regel bryts (bara försök 1),
#   - godkänd, när bara testfiler har ändrats och bygget är grönt.
# Utdata på sista raden: AVGJORD, KLAR_FOR_GRANSKNING eller CLAUDE.
# Ändras bara av Claude. Se ARBETSFLODE.md, "Spärren" och "Reservläge".
set -uo pipefail

GREN="$1"; BYGGE="$2"
namn="${GREN#lokal/}"
uppg="uppgifter/pagar/$namn.md"
[ -f "$uppg" ] || uppg="uppgifter/ny/$namn.md"
[ -f "$uppg" ] || { echo "Uppgiftsfilen saknas på grenen."; echo CLAUDE; exit 0; }

# En fråga från den lokala modellen kräver omdöme.
[ -f FRAGA.md ] && { echo "FRAGA.md finns."; echo KLAR_FOR_GRANSKNING; exit 0; }

git fetch -q --depth=200 origin main 2>/dev/null || git fetch -q origin main
bas=$(git merge-base origin/main HEAD)
andrade=$(git diff --name-only "$bas" HEAD | grep -vE '^(uppgifter|granskning)/' || true)
tillatna=$(awk '/^## Ändra bara dessa filer/{f=1;next} /^## /{f=0} f && /^- /' "$uppg" \
  | sed -E 's/^- +//; s/`//g; s/ +\(.*\)$//; s/[[:space:]]+$//')

fel=()
[ -z "$andrade" ] && fel+=("Inga filer är ändrade.")
while IFS= read -r f; do
  [ -z "$f" ] && continue
  grep -qxF "$f" <<<"$tillatna" || fel+=("Filen \`$f\` står inte under \"Ändra bara dessa filer\" (fel sökväg eller fel fil).")
done <<<"$andrade"
while IFS= read -r f; do
  [ -z "$f" ] && continue
  if [ ! -f "$f" ]; then fel+=("Filen \`$f\` finns inte.")
  elif [ ! -s "$f" ]; then fel+=("Filen \`$f\` är tom.")
  fi
done <<<"$tillatna"
# Uppgifter som skriver tester till befintlig kod listar ofta även koden; det räcker att något av det tillåtna har ändrats.
if [ -n "$andrade" ] && ! grep -qxFf <(printf '%s\n' "$tillatna") <<<"$andrade"; then
  fel+=("Ingen av filerna under \"Ändra bara dessa filer\" är ändrad.")
fi
rader=$(git diff --numstat "$bas" HEAD -- $andrade 2>/dev/null | awk '{s+=$1+$2} END{print s+0}')
[ "$rader" -gt 250 ] && fel+=("$rader ändrade rader, gränsen är cirka 150.")
if git diff "$bas" HEAD -- $andrade | grep -E '^\+' | grep -qE 'uses-permission|android\.permission\.INTERNET'; then
  fel+=("En behörighet har lagts till.")
fi
if git diff "$bas" HEAD -- $andrade | grep -E '^\+' | grep -qE '^\+\s*(implementation|api|ksp|kapt|testImplementation)\('; then
  fel+=("Ett beroende har lagts till.")
fi
[ "$BYGGE" = "failure" ] && fel+=("Bygget eller testerna är röda. Kör testet i uppgiften och rätta tills det är grönt. Testerna i appmodulerna använder JUnit 4: import org.junit.Test och import org.junit.Assert.assertEquals (eller assertTrue, assertFalse), inte kotlin.test.")

if [ ${#fel[@]} -gt 0 ]; then
  printf '%s\n' "${fel[@]}"
  case "$namn" in *-f2|*-f3)
    echo "Försök 2 bröt mot spärren. Lämnas åt Claude."; echo CLAUDE; exit 0;;
  esac
  {
    echo "beslut: underkänd"
    echo "granskare: spärren"
    echo "datum: $(date +%F)"
    echo; echo "## Kontrollerat"; printf -- '- %s\n' "${fel[@]}"
    echo; echo "## Rättat av Cursor"; echo "- inget"
    echo; echo "## Vid underkänd: försök 2"
    sed -n '1p' "$uppg"
    echo; echo "## Vad som saknades"; printf -- '- %s\n' "${fel[@]}"
    sed '1d' "$uppg" | sed 's/^försök: 1/försök: 2/'
    echo; echo "## Frågor till Claude"; echo "- inga"
  } > "granskning/$namn.md"
  echo AVGJORD; exit 0
fi

# Bara testfiler och grönt bygge: godkänns utan AI. Claude stickprovar i efterhand.
if [ "$BYGGE" = "success" ] && ! grep -qv '/src/test/' <<<"$andrade"; then
  mkdir -p uppgifter/klar
  git mv "$uppg" "uppgifter/klar/$namn.md"
  printf '\nutfall: godkänd %s av spärren (bara tester, grönt bygge)\n' "$(date +%F)" >> "uppgifter/klar/$namn.md"
  {
    echo "beslut: godkänd"
    echo "granskare: spärren"
    echo "datum: $(date +%F)"
    echo; echo "## Kontrollerat"
    echo "- Bara testfiler under \"Ändra bara dessa filer\" är ändrade, $rader rader."
    echo "- Inga behörigheter eller beroenden. Bygget och testerna är gröna."
    echo; echo "## Rättat av Cursor"; echo "- inget"
    echo; echo "## Frågor till Claude"; echo "- inga"
  } > "granskning/$namn.md"
  echo AVGJORD; exit 0
fi

echo "Spärren godkänner. Behöver granskning av kod."
echo KLAR_FOR_GRANSKNING
