# Uppdrag: Röstfångst Del A under reservläget (2026-10-27)

Skrivet av Claude som uppgifter direkt till den lokala modellen (Cursor är i reservläge, CURSOR-PAUS). Grunduppdraget är `uppdrag/2026-10-25-rostfangst-home-assistant.md`; beslut och "Gör inte" där gäller.

## Läget
- Typen HEM finns redan i `ForslagTyp`, `tolkaSvar`, systemprompten, etiketten och `HandlingUtforare` (gör ingenting än).
- Hjälpfunktionerna i `ha/` finns: `normaliseraHaAdress`, `bygghaHandelseJson`, `byggTodoJson`, `haFelText`, `lasAssistSvar`.
- Snabbstarten finns: rutan i snabbinställningarna öppnar `MainActivity` med `EXTRA_START_INSPELNING`, som startar inspelningen direkt. Mattias verifierar på telefon.

## Uppgifter (`uppgifter/ny/2026-10-27-01` till `-08`, i ordning)
1. `HaKlient` och `HaHttpKlient` med falsk transport i test.
2. `HaKonfig` och `haSteg`: vilka HA-steg en typ ger.
3. `korHaSteg`: kör stegen mot en `HaKlient` och samlar svar och fel.
4. HA-inställningar i `InstallningarLager` (Robolectric-test).
5. `usesCleartextTraffic` i manifestet (provappen, avsteg i APP.md).
6. ViewModel: HA-inställningar och HA-stegen efter "Utför".
7. Inställningsskärmen: HA-fälten, och rätt etikett på varje typ-chip.
8. Meddelanden för fel HA-adress och saknad HA. HA:s svar och fel visas i snackbaren.

## Väntar på Cursor (efter cirka 9 november)
- Status per fångst mot HA och "Skicka igen" i historiken: kräver ny kolumn och databasmigration.

## Klart när
Alla åtta är mergade med grönt bygge, och kraven i grunduppdragets "Klart när" utom "Skicka igen" är uppfyllda.
