# Butiksmaterial — Husloggen (arbetsnamn)

Filer för Google Play Console. Mattias kopierar texterna, skärmbilderna, funktionsgrafiken och ikonen till Play; inget laddas upp automatiskt från repot. Appnamn och applicationId beslutas vid publicering (CLAUDE.md punkt 9); texterna är preliminära tills dess.

## Texter

| Språk | Filer |
|-------|--------|
| Svenska (butikens huvudspråk) | `sv/titel.txt`, `sv/kort.txt`, `sv/lang.txt` |
| Engelska | `en/titel.txt`, `en/kort.txt`, `en/lang.txt` |

Teckengränser och URL-förbud kontrolleras av `ButikTexterTest`.

## Bilder

| Fil / mapp | Storlek | Innehåll |
|------------|---------|----------|
| `skarmbilder/sv/` | 1080 × 1920 (porträtt) | Att göra, Logg, formulär, Sammanställning, Pro-dialog |
| `skarmbilder/en/` | 1080 × 1920 | Samma skärmar på engelska |
| `funktionsgrafik-sv.png` | 1024 × 500 | Appnamn och kort rad på svenska (från strängresurser) |
| `funktionsgrafik-en.png` | 1024 × 500 | Appnamn och kort rad på engelska |
| `ikon-512.png` | 512 × 512 | Butiksikon renderad från launcher-grafik |

Alla skärmbilder använder påhittade uppgifter och poster (inga riktiga adresser eller varumärken).

## Data safety

Se `data-safety.md` för svar och motivering till Play Console-formuläret.

## Återskapa skärmbilder och grafik

Kör från repots rot (kräver Android SDK enligt CI):

```bash
./gradlew :husloggen:recordRoborazziDebug
```

Det kör Roborazzi-testerna i `:husloggen` och skriver PNG-filer till denna mapp (`butik/skarmbilder/`, plus `funktionsgrafik-sv.png`, `funktionsgrafik-en.png` och `ikon-512.png` i `butik/`).

Verifiera att bilderna är oförändrade:

```bash
./gradlew :husloggen:verifyRoborazziDebug
```

Kör även enhetstesterna för texterna:

```bash
./gradlew :husloggen:testDebugUnitTest --tests "*ButikTexterTest"
```
