# Arbetsflöde: granskning och frågor

Kompletterar CLAUDE.md punkt 3. Gäller från 2026-10-09.

## Granskning i två led

1. **Den lokala modellen** öppnar en PR från `lokal/<uppgift>`.
2. **Cursor granskar** den lokala modellens PR. Workflowet `cursor-granskning` startar en Cursor-agent på samma gren när bygget är klart. Cursor:
   - rättar små fel direkt på grenen,
   - skriver sitt beslut i `granskning/<uppgift>.md` på grenen (format nedan),
   - flyttar vid godkännande uppgiftsfilen från `uppgifter/pagar/` till `uppgifter/klar/` med raden `utfall: godkänd ÅÅÅÅ-MM-DD`.
3. **Claude granskar Cursor.** Claudes timvisa granskning läser Cursors beslut, gör en stickprovskontroll av koden och:
   - mergar när beslutet är godkänt och bygget grönt,
   - stänger PR:en och lägger Cursors försök 2 på main när beslutet är underkänt,
   - underkänner Cursors beslut om det är fel, och skriver varför i BESLUTSLOGG.md.
4. Cursors egna PR:er (`cursor/...`) granskas och mergas av Claude, som tidigare.

## Beslutsfil `granskning/<uppgift>.md`

```
beslut: godkänd | underkänd | fråga
granskare: cursor
datum: ÅÅÅÅ-MM-DD

## Kontrollerat
- (punkterna i CLAUDE.md punkt 8, en rad var)

## Rättat av Cursor
- (eller "inget")

## Vid underkänd: försök 2
(hela innehållet i nästa uppgiftsfil, enligt uppgifter/MALL.md, med avsnittet "Vad som saknades" överst)

## Frågor till Claude
- (eller "inga")
```

## Frågor

Ingen agent kan prata direkt med en annan. Frågor skrivs därför på fasta ställen, där mottagaren alltid tittar.

| Vem frågar | Var | Vem svarar | Hur svaret kommer |
|---|---|---|---|
| Lokala modellen | `FRAGA.md` i repots rot på sin gren, i stället för kod | Cursor | Ett försök 2 där frågan är besvarad i uppgiften |
| Cursor | "Frågor till Claude" i beslutsfilen eller i PR-beskrivningen | Claude | Ett nytt uppdrag, en ändrad uppgift eller ett svar i BESLUTSLOGG.md |
| Claude | `FRAGOR.md` i repots rot, under "Öppna" | Mattias | Mattias svarar i chatten. Claude flyttar frågan till "Besvarade" |

Regler:
- En fråga ska gå att besvara kort. Den som frågar gör allt som inte beror på svaret.
- Den lokala modellen frågar bara när uppgiften är motsägelsefull eller pekar på filer som inte finns. Hellre en fråga än gissad kod.
- Cursor frågar Claude bara om sådant som rör uppdraget eller direktiven, inte om kod.
- Claude frågar Mattias bara om det som CLAUDE.md punkt 9 kräver, eller om det inte går att ångra.
