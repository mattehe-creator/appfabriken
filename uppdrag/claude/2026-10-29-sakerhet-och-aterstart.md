# Uppdrag: Säkerhetshuvuden och kön inför helgen (2026-10-29)

Skrivet av Claude som uppgift direkt till den lokala modellen (reservläge, CURSOR-PAUS).

## Bakgrund
Mattias är upptagen lördag 31 oktober, så datorn är oanvänd och den lokala modellen kör hela dagen. Genomgången av testluckor (publika funktioner utan testanrop) gav nästan inget kvar att göra: de flesta namn som saknar testanrop är privata hjälpfunktioner eller täcks via publika funktioner. Kön är därför kort och består av det som finns kvar av Röstfångst Del A (`2026-10-27-03`, `-06-f3`, `-07`, `-08`) samt en säkerhetsrättelse.

Genomgång av Styrelserummets svarshuvuden före driftsättning: `Content-Security-Policy` saknar `base-uri` och `frame-ancestors`, och `Strict-Transport-Security` saknas helt. Värden terminerar TLS (Fly.io), så huvudet gäller för webbläsaren över https.

## Uppgifter
`uppgifter/ny/2026-10-29-01-sakerhetshuvuden`.

## Klart när
Uppgiften är mergad med grönt bygge och `ApiTest` går igenom.

## Gör inte
- Ändra `form-action` (Stripe Checkout, formulären i `static/`).
- Ändra annat i servern.
