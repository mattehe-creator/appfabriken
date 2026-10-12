# Butiksmaterial — Garantivalvet

Filer för Google Play Console. Mattias kopierar texterna, skärmbilderna, funktionsgrafiken och ikonen till Play; inget laddas upp automatiskt från repot.

## Texter

| Språk | Filer |
|-------|--------|
| Svenska (butikens huvudspråk) | `sv/titel.txt`, `sv/kort.txt`, `sv/lang.txt` |
| Engelska | `en/titel.txt`, `en/kort.txt`, `en/lang.txt` |

Teckengränser och URL-förbud kontrolleras av `ButikTexterTest`.

## Bilder

| Fil / mapp | Storlek | Innehåll |
|------------|---------|----------|
| `skarmbilder/sv/` | 1080 × 1920 (porträtt) | Lista, detalj med reklamationsinfo, formulär, Pro-dialog |
| `skarmbilder/en/` | 1080 × 1920 | Samma skärmar på engelska |
| `funktionsgrafik.png` | 1024 × 500 | Appnamn och kort tagline i appens tema |
| `ikon-512.png` | 512 × 512 | Butiksikon renderad från launcher-grafik |

Alla skärmbilder använder påhittade köp (t.ex. diskmaskin, mobiltelefon, cykel).

## Data safety

Se `data-safety.md` för svar och motivering till Play Console-formuläret.

## Återskapa skärmbilder och grafik

Kör från repots rot (kräver Android SDK enligt CI):

```bash
./gradlew :garantivalvet:recordRoborazziDebug
```

Det kör Roborazzi-testerna i `:garantivalvet` och skriver PNG-filer till denna mapp (`butik/skarmbilder/`, plus `funktionsgrafik.png` och `ikon-512.png` i `butik/`).

Verifiera att bilderna är oförändade:

```bash
./gradlew :garantivalvet:verifyRoborazziDebug
```

Kör även enhetstesterna för texterna:

```bash
./gradlew :garantivalvet:testDebugUnitTest --tests "*ButikTexterTest"
```
