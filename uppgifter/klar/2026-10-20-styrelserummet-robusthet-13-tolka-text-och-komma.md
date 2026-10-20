# Uppgift: Text runt JSON, kommatecken, null och taltext

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslag.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslagTest.kt

## Uppgift
Utöka `tolkaForslag` och `heltalsVarde` så att:
- JSON inlindad i markdown-kodstaket med taggen `json` extraheras före parsning,
- kort text **före eller efter** JSON-objektet ignoreras,
- **avslutande kommatecken** accepteras,
- JSON `null` på talfält behandlas som saknat värde,
- tal som textsträngar `"1 200 000"` / `"1200000"` blir heltal.

Ogiltig JSON eller ogiltiga talsträngar ska ge anmärkning och `Sakerhet.LAG`, aldrig okontrollerat undantag.

## Exempel
- Indata: wrapped in ` ```json ... ``` ` with en komponent → samma resultat som rå JSON.
- Indata: `Notera detta {"komponenter":[],"anmarkningar":[]}` → tom lista.
- Indata: `{"komponenter":[{"namn":"Tak","grupp":"TAK","anskaffningsvarde":"1 200 000","anskaffningsar":2000,"nyttjandeperiodAr":40,"kalla":"p","sakerhet":"MEDEL"}],"anmarkningar":[]}` → `1200000L`.
- Indata: `"anskaffningsvarde": "abc"` → anmärkning, `LAG`.
- Indata: `inte json alls` → anmärkning om ogiltig JSON.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*TolkaForslagTest.tolkaRobustJsonOchTal"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

utfall: underkänd 2026-10-20 av Cursor (PR #303). Ersatt av 2026-10-20-styrelserummet-robusthet-13-tolka-text-och-komma-f2.md
