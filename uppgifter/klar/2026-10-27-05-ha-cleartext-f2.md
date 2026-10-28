# Uppgift: Tillåt http mot Home Assistant på lokalt nät (Röstfångst)

## Vad som saknades
- Inga filer är ändrade.

uppdrag: uppdrag/claude/2026-10-27-rostfangst-ha-reserv.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- rostfangst/src/main/AndroidManifest.xml
- rostfangst/src/test/kotlin/se/tmconnect/rostfangst/NatverkBehorighetTest.kt

## Uppgift
Röstfångst är en provapp som inte publiceras. Home Assistant på hemnätet nås ofta över `http://192.168.x.x:8123`, vilket Android blockerar som standard. Avsteget är beslutat och står i `rostfangst/APP.md`.

1. Lägg till attributet `android:usesCleartextTraffic="true"` i `<application>` i manifestet, direkt efter `android:allowBackup="false"`. Inget annat ändras.
2. Lägg till ett testfall i `NatverkBehorighetTest`, i samma stil som det befintliga:

@Test
fun klartextTillatsForHemmanatet() {
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    val flaggor = context.applicationInfo.flags
    assertTrue(
        "usesCleartextTraffic krävs för Home Assistant över http",
        flaggor and android.content.pm.ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC != 0,
    )
}

## Exempel
- Före ändringen: nya testet är rött.
- Efter: båda testen i `NatverkBehorighetTest` är gröna.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*NatverkBehorighetTest"

## Gör inte
- Ändra andra filer eller andra attribut i manifestet.
- Lägga till behörigheter eller beroenden.

utfall: godkänd 2026-10-28 (Claude tog över, reservläge)
