# Uppgiftsformat

En uppgift är en fil i `uppgifter/ny/`, skriven av Cursor utifrån ett uppdrag. Den lokala modellen ser bara uppgiftsfilen och de filer som listas i den, så allt den behöver måste stå här. Filnamnet är `ÅÅÅÅ-MM-DD-NN-kort-namn.md`, där NN är ordningen inom uppdraget. Körskriptet tar filerna i namnordning.

Kraven i CLAUDE.md punkt 4 gäller: högst 3 ändrade filer, cirka 150 rader, minst ett test, inga nya beroenden.

---

```
# Uppgift: <kort namn>

uppdrag: uppdrag/<filnamn>.md
försök: 1
beror-på: <tidigare uppgiftsfil, eller "ingen">

## Ändra bara dessa filer
- garantivalvet/src/main/java/.../Fil.kt (ny)
- garantivalvet/src/test/java/.../FilTest.kt (ny)

## Läs som förebild (ändra inte)
- garantivalvet/src/main/java/.../Befintlig.kt

## Uppgift
Beskriv beteendet i konkreta steg. Ange namn på klasser och funktioner,
och deras signaturer.

## Exempel
- Indata: ... Förväntat: ...
- Indata: ... Förväntat: ...

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*FilTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
```

---

## När försök 1 underkänns

Cursor skriver försök 2 i sin beslutsfil `granskning/<uppgift>.md` (se ARBETSFLODE.md), och Claude lägger det som en ny fil med samma namn och `-f2` sist, med `försök: 2` och ett avsnitt **Vad som saknades** överst. Den gamla filen flyttas till `uppgifter/klar/` med raden `utfall: underkänd` sist. Underkänns försök 2 gör Cursor uppgiften själv.
