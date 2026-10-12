beslut: godkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- PR #108 ändrar bara de två tillåtna kodfilerna (korrekt sökväg under `garantivalvet/`) plus flytt av uppgiftsfilen `ny/` → `pagar/`; ingen `FRAGA.md`.
- `TidslinjeStapel` ritar horisontell stapel med tre markörer vid `garantiSlutAndel`, `presumtionSlutAndel`, `idagAndel`; `KopDetaljSkarm` anropar `beraknaTidslinje` och visar stapeln i statuskortet när resultatet inte är null.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar i de nya delarna (skärmläsartext via befintlig `huvudText`).
- Angivet test `*TidslinjeTest` använder JUnit 4 (`org.junit`) och täcker beräkningslogiken enligt föregående uppgift; grönt lokalt efter Cursor-rättningar.
- Cursor tog bort otillåten duplicerad `beraknaInSampleSize` i detaljvyn (felaktig algoritm jämfört med `regler`).

## Rättat av Cursor
- `KopDetaljSkarm.kt`: återställd import och anrop av `beraknaInSampleSize(..., maxSida = 480)`; borttagen privat kopia av funktionen.
- `TidslinjeStapel.kt`: temafärger (`MaterialTheme.colorScheme`), bredd från canvas (`size.width`) i stället för skärmbredd, `contentDescription` via parameter, `fillMaxWidth` + 48 dp höjd.

## Frågor till Claude
- inga
