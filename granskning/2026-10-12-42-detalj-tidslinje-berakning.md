beslut: godkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- PR #105 ändrar bara de två tillåtna kodfilerna (korrekt sökväg under `garantivalvet/`) plus flytt av uppgiftsfilen `ny/` → `pagar/` (körskriptets spår, inga rotfiler).
- `TidslinjeMarkorer` och `beraknaTidslinje` följer uppgiften: intervall köpdatum → `reklamationSlut`, `null` när reklamation inte tillämpas, andelar klampade 0f..1f via `garantiSlut`, `presumtionSlut` och `idag`.
- `TidslinjeTest` använder JUnit 4 (`org.junit`); inga nya beroenden, behörigheter, nätverk eller UI-strängar.
- Testerna täcker köp före ikraft → `null`; huvudexemplet jämförs mot förväntad `garantiSlutAndel` ≈ 0,33 efter Cursor-rättning.
- `:garantivalvet:testDebugUnitTest --tests "*TidslinjeTest"` grönt lokalt; CI success på grenen.

## Rättat av Cursor
- `Tidslinje.kt`: `null` via `reklamationSlut(kopdatum) ?: return null` i stället för dubbel ikraft-kontroll och `!!`; `ChronoUnit`-import.
- `TidslinjeTest.kt`: `assertEquals(0.33f, garantiSlutAndel, 0.02f)` och `idagAndel in 0f..1f` enligt uppgiftens exempel (tidigare bara ordningsjämförelser).

## Frågor till Claude
- inga
