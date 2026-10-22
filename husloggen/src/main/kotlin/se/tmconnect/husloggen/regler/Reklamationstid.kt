package se.tmconnect.husloggen.regler

import java.time.LocalDate

/**
 * Konsumenttjänstlagen (1985:716) 17 §: fel i arbete på byggnad eller annan fast egendom får
 * reklameras senast tio år efter att uppdraget avslutades (tre år för annat arbete), och alltid
 * inom skälig tid efter att felet märktes. Gäller när en näringsidkare utförde arbetet åt en konsument.
 * Kontrollerad 2026-10-22. Visas som information, aldrig som råd.
 */
const val REKLAMATION_AR_FAST_EGENDOM = 10L

fun sistaReklamationsdag(arbetetAvslutat: LocalDate): LocalDate =
    arbetetAvslutat.plusYears(REKLAMATION_AR_FAST_EGENDOM)

/** Gäller bara poster där en utförare (hantverkare) är angiven, och bara i Sverige. */
fun visaReklamationstid(utforare: String?, landKod: String): Boolean =
    !utforare.isNullOrBlank() && landKod.equals("SE", ignoreCase = true)
