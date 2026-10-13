# Uppgift: föreslå varutyp i formuläret när användaren skrivit varunamnet

uppdrag: ingen (Mattias önskemål 2026-10-12: appen ska hjälpa till att välja varutyp)
försök: 1
beror-på: 2026-10-13-01-varutyp-gissning.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopFormularSkarm.kt
- garantivalvet/src/main/res/values/strings.xml
- garantivalvet/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/VarutypGissning.kt (funktionen `gissaVarutyp(vad: String): Varutyp?`, finns efter föregående uppgift)

## Uppgift
I `KopFormularSkarm` styr varutypen frister (till exempel är mat och dryck färskvaror). Låt formuläret föreslå en varutyp när användaren skrivit varans namn, utan att ta över.

Beteende:
1. Lägg till tillståndet `var varutypForslag by remember { mutableStateOf(false) }` bredvid `varutyp`.
2. När `vad` ändras (`LaunchedEffect(vad)`): om användaren inte själv har valt en varutyp (alltså `varutyp == null`, eller `varutypForslag` är sant) sätts `varutyp = gissaVarutyp(vad)` och `varutypForslag = (varutyp != null)`. Är varutypen vald av användaren (`varutyp != null` och `varutypForslag` falskt) ändras ingenting.
3. När användaren själv trycker på en varutyp-chip (där `onClick` idag sätter `varutyp`) sätts `varutypForslag = false`.
4. Vid redigering av ett befintligt köp (`initialVarutyp != null`) räknas varutypen som vald av användaren: `varutypForslag` startar som falskt, och ett befintligt val skrivs aldrig över.
5. När `varutypForslag` är sant visas en liten text under raden med varutyp-chips (`Text` med `MaterialTheme.typography.bodySmall`, färg `onSurfaceVariant`) med den nya strängen `varutyp_forslag`.
6. Lägg till strängen `varutyp_forslag` i båda strängfilerna:
   - sv: `Förslag utifrån namnet – ändra om det inte stämmer`
   - en: `Suggested from the name – change it if it is wrong`
7. Inget annat ändras. Hårdkodad text är förbjuden; allt ligger i `strings.xml`.

## Exempel
- Nytt köp, användaren skriver "Kaffebryggare": chipen "Elektronik" blir vald och texten "Förslag utifrån namnet – ändra om det inte stämmer" visas.
- Användaren skriver vidare till "Kaffebryggare Philips": fortfarande Elektronik, texten visas.
- Användaren trycker själv på "Kläder & skor": valet byts och texten försvinner. Skriver hen sedan "Mjölk" ändras inget (hens eget val behålls).
- Användaren skriver "Present": inget förslag (`gissaVarutyp` ger `null`), `varutyp` förblir `null`, ingen text.
- Användaren trycker själv på den valda chipen igen (avmarkerar): `varutyp` blir `null`, texten försvinner, och nästa ändring av namnet får föreslå igen.
- Ändra ett sparat köp med varutypen "Mat och dryck": inget skrivs över, ingen text.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"
gradle :garantivalvet:testDebugUnitTest --tests "*ButikSkarmbilder*"
gradle testDebugUnitTest

## Gör inte
- Ändra andra filer eller skärmbilderna i `butik/`.
- Lägga till beroenden.
- Ändra databasschemat.
- Ändra `onSpara` eller andra funktioners signaturer.
