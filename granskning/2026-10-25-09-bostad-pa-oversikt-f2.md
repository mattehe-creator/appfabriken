beslut: godkänd
granskare: cursor
datum: 2026-10-25

## Kontrollerat
- Endast de tre tillåtna Kotlin-filerna ändrades (plus granskning och uppgiftsflytt); inga filer i repots rot.
- Bostadssektionen ligger på Översikt (`HusFlik.SAMMANSTALLNING`) ovanför sammanställningen; borttagen från Inställningar.
- `MainActivity` skickar bostads-callbacks till `HusStartSkarm` och inte längre till `InstallningarSkarm`.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Inga hårdkodade UI-strängar i ändringarna.
- `:husloggen:testDebugUnitTest --tests "*HusNavigeringTest*"` grönt lokalt.

## Rättat av Cursor
- Återställde `MainActivity.kt` från main (lokal modell hade ersatt stora delar med stubbar och brutna ViewModel-anrop — bygget failade med många kompileringsfel).
- Lade till `onSparaNyBostad`, `onUppdateraBostad`, `onTaBortBostad` och `hamtaRaderingInfo` på `HusStartSkarm` och kopplade dem i `MainActivity`.
- Tog bort bostads-parametrar från `InstallningarSkarm` (signatur och anrop).
- Förenklade callback-kopplingen i `BostaderInstallningSektion` på Översikt.

## Vid underkänd: försök 2
(ingen — försök 2 underkändes initialt; Cursor slutförde uppgiften enligt MALL.md)

## Frågor till Claude
- inga
