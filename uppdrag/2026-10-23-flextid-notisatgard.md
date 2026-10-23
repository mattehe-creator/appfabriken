# Uppdrag: Flextid, åtgärdsknappar i påminnelsenotiserna

## Bakgrund

Flextid har påminnelserna "Glöm inte stämpla ut" och "Glöm inte stämpla in" (flextid/APP.md) och, sedan PR #623, genvägar som stämplar in och ut via `FlextidRepository.genvagStamplaIn` och `genvagStamplaUt`. Nästa steg är att användaren kan stämpla direkt från notisen utan att öppna appen. Inget nätverk, ingen ny behörighet (`POST_NOTIFICATIONS` finns redan), inget nytt beroende.

## Uppgift

Cursor delar upp arbetet i små uppgifter för den lokala modellen (uppgifter/MALL.md, högst 3 filer och cirka 150 rader per uppgift) och gör själv det som rör manifest, BroadcastReceiver och notisbygget.

1. Notisen "Glöm inte stämpla ut" får en åtgärdsknapp "Stämpla ut". Notisen "Glöm inte stämpla in" får en knapp "Stämpla in". Etiketter i `strings.xml` på svenska och engelska (återanvänd `stampla_in` och `stampla_ut`).
2. Knappen skickar en `PendingIntent` (FLAG_IMMUTABLE) till en `BroadcastReceiver` som anropar samma repository-metoder som genvägarna (`genvagStamplaIn` och `genvagStamplaUt`, med klockslaget vid tryckningen). Ingen ny affärslogik i receivern. Receivern är inte exporterad.
3. Resultatet: sparat, redan instämplad, ingen instämpling, slut före start eller gratisgräns. Notisen tas bort vid "sparat". Vid övriga utfall ersätts texten i notisen med ett kort meddelande (återanvänd strängarna `genvag_redan_instamplad`, `genvag_ingen_instempling`, `fel_slut_fore_start`, `pro_grans_nadd`) och knappen tas bort.
4. Receivern får inte starta något tungt arbete på huvudtråden: använd `goAsync()` med en coroutine, eller WorkManager om det är enklare att testa.
5. Ren Kotlin i `regler/` för valet av notistext och om notisen ska tas bort, enhetstestad med konkreta fall (indata: resultatet från repository, utdata: ta bort notisen eller visa textnyckel).

## Klart när

- Enhetstester för punkt 5 med minst ett fall per utfall i punkt 3.
- Test av receiverns intent-tolkning (åtgärdsnamn in och ut, okänd åtgärd ignoreras).
- `gradle :flextid:testDebugUnitTest` och bygget är gröna.

## Gör inte

- Ändra databasschemat, Garantivalvet, Husloggen eller `:karna` (om `:karna` måste röras: stanna och skriv det under "Frågor till Claude").
- Lägga till widget (kräver Glance, eget uppdrag), dynamiska genvägar eller nya notiskanaler.
- Ändra `.github/workflows/`, `verktyg/`, `CLAUDE.md`, `STATUS.md`, `BESLUTSLOGG.md`, `IDEURVAL.md`.
- Kräva svar från Mattias. Frågor skrivs under "Frågor till Claude" i PR-beskrivningen.
