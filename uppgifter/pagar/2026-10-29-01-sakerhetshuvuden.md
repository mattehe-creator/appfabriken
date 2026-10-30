# Uppgift: Skärp säkerhetshuvudena i Styrelserummet (HSTS, base-uri, frame-ancestors)

uppdrag: uppdrag/claude/2026-10-29-sakerhet-och-aterstart.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Svarshuvuden.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/SvarshuvudenSkarpningTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/SvarshuvudenTest.kt (befintliga tester ska fortsätta gå igenom)

## Uppgift
Två ändringar i `Svarshuvuden.kt`:

1. Konstanten `CSP_STYRELSERUMMET` får två direktiv sist, så att den blir exakt:
   `default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self'; base-uri 'none'; frame-ancestors 'none'`
   (`base-uri 'none'` hindrar injicerad `<base>`, `frame-ancestors 'none'` hindrar att sidorna läggs i en ram hos någon annan). Lägg inte till `form-action`.
2. `gemensammaSvarshuvuden()` får ett fjärde huvud: `Strict-Transport-Security` med värdet `max-age=31536000`. Inte `includeSubDomains`, inte `preload`.

Skapa den nya testfilen `SvarshuvudenSkarpningTest.kt` (ny, JUnit 4, `org.junit.Test`, `org.junit.Assert.*`) i samma paket, med klassen `SvarshuvudenSkarpningTest`.

## Exempel
- `CSP_STYRELSERUMMET` är exakt strängen ovan.
- `svarshuvudenForSokvag("/")["Strict-Transport-Security"]` är `"max-age=31536000"`.
- `svarshuvudenForSokvag("/api/jobb/abc")["Strict-Transport-Security"]` är `"max-age=31536000"` (gäller alla sökvägar).
- `svarshuvudenForSokvag("/")["Content-Security-Policy"]` innehåller `frame-ancestors 'none'` och `base-uri 'none'` och innehåller inte `form-action`.
- `gemensammaSvarshuvuden().keys` är exakt `Referrer-Policy`, `X-Content-Type-Options`, `Content-Security-Policy`, `Strict-Transport-Security`.
- `gemensammaSvarshuvuden()["Strict-Transport-Security"]` innehåller inte `includeSubDomains`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*SvarshuvudenSkarpningTest" --tests "*SvarshuvudenTest" --tests "*ApiTest"

## Gör inte
- Ändra andra filer än de två ovan.
- Lägga till beroenden.
- Ändra `SvarshuvudenTest.kt` eller `ApiTest.kt`.
