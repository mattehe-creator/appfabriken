package se.tmconnect.garantivalvet.regler

import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

/**
 * Mat och dryck är färskvaror. Konsumentverket ("Handla mat i butik",
 * https://www.konsumentverket.se/varor-och-tjanster/handla-mat-i-butik/): tiden att klaga blir då
 * "betydligt kortare", varan ska kontrolleras direkt och butiken kontaktas omedelbart. Någon fast
 * frist i dagar finns inte, så appen räknar inte ut någon och visar varken garanti eller
 * treårig reklamationsfrist för färskvaror.
 */
fun arFarskvara(varutyp: String?): Boolean = Varutyp.franNamn(varutyp) == Varutyp.MAT_DRYCK

fun arFarskvara(kop: Kop): Boolean = arFarskvara(kop.varutyp)

/** Status för ett köp: färskvaror får egen status, övriga räknas ur garanti och reklamationsrätt. */
fun statusForKop(kop: Kop, idag: LocalDate): KopStatus =
    if (arFarskvara(kop)) KopStatus.FARSKVARA else kopStatus(kop.kopdatum, kop.garantiManader, idag)
