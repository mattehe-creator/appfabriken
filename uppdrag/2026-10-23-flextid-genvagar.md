# Uppdrag: Flextid, genväg för att stämpla in och ut

## Bakgrund

Flextid (app 3, flextid/APP.md) används varje dag, och det som tar tid är att öppna appen och hitta rätt knapp. En app-genväg (långtryck på appikonen) gör att användaren kan stämpla in eller ut med två tryck. Inget nätverk, ingen ny behörighet.

## Uppgift

Cursor delar upp arbetet i små uppgifter för den lokala modellen (uppgifter/MALL.md, högst 3 filer och cirka 150 rader per uppgift) och gör själv det som rör manifest, genvägar och aktivitetskopplingen.

1. Två statiska genvägar (`shortcuts.xml`, `res/xml/`): "Stämpla in" och "Stämpla ut". Etiketter i `strings.xml` på svenska och engelska, inga hårdkodade texter.
2. Genvägen öppnar `MainActivity` med en åtgärd. Appen stämplar in (startar dagens post med nuvarande tid) eller ut (sätter sluttid) via befintlig regelkod i `regler/` och repository. Ingen ny affärslogik i UI-lagret.
3. Regler med exempel (ren Kotlin, enhetstestas):
   - Stämpla in när dagen saknar start: start = nu avrundad till hel minut.
   - Stämpla in när dagen redan har start: ändra ingenting, visa meddelandet "Redan instämplad kl. HH:mm".
   - Stämpla ut när dagen har start men inte slut: slut = nu. Slut före start tillåts inte (befintlig validering gäller).
   - Stämpla ut när dagen saknar start: ändra ingenting, visa meddelandet att ingen instämpling finns.
   - Gratisgränsen (15 dagposter) gäller som vanligt: ny dag över gränsen skapas inte, och befintligt meddelande visas.
4. Efter åtgärden visas en kort bekräftelse (Snackbar eller toast) och appen visar Idag-fliken.
5. Ingen ny behörighet, inget nätverk, inget nytt beroende, ingen ändring av databasschemat.

## Klart när

- Enhetstester för reglerna i punkt 3 med konkreta klockslag och datum.
- `gradle :flextid:test` och bygget är gröna. Testerna för övriga appmoduler är gröna om `:karna` rörs.
- Genvägarna finns i APK:n och öppnar rätt åtgärd (test av intent-tolkningen räcker).

## Gör inte

- Ändra databasschemat, Garantivalvet eller Husloggen.
- Lägga till widget, notisåtgärder eller dynamiska genvägar (eget uppdrag om det behövs).
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
- Kräva svar från Mattias. Frågor skrivs under "Frågor till Claude" i PR-beskrivningen.
