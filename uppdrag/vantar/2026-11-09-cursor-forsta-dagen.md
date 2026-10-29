# Cursors prioritering när kvoten förnyats (cirka 2026-11-09)

Skrivet av Claude 2026-10-29. Ligger i `vantar/` och skickas inte. När `CURSOR-PAUS` tas bort tar Claude uppdragen i den här ordningen, ett i taget, så att kvoten inte tar slut förrän ungefär mitt i månaden igen. Cursor har bara ungefär 15 dagars kvot per månad med nuvarande volym (BESLUTSLOGG.md 2026-10-25).

1. **Röstfångst: status per fångst och "Skicka igen"** (kräver ny kolumn och migration, bara Cursor). Förutsättning: `2026-10-25-rostfangst-home-assistant` Del A mergad.
2. **Lyft till `:karna`** (kvitton, avläsning, påminnelser, säkerhetskopia, formatering) för det Husloggen och Flextid redan har gemensamt. Bara en ändring i taget, alla appmodulers tester gröna.
3. **Röstfångst lokal AI Del A** (`vantar/2026-10-25-rostfangst-lokal-ai.md`): `ServerRostLyssnare`, inställningar, `verktyg/whisper/`, `rostfangst/LOKAL-AI.md`. `OllamaTolkare` och hjälpfunktionerna finns redan.
4. **Röstfångst klocka Del A** (`vantar/2026-10-25-rostfangst-klocka.md`): ny modul `:rostfangst-wear`, nya beroenden är godkända i uppdraget.
5. **Garantivalvet 1.1**: byt till de delar som lyfts till `:karna`, först när 1.0 är publicerad.

Det som inte kräver Cursor (uppgifter om högst tre filer) fortsätter Claude dela upp åt den lokala modellen även efter förnyelsen.
