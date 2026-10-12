package se.tmconnect.garantivalvet.regler

/**
 * Grader (medurs) som en bild ska roteras för att stå rätt, enligt EXIF-orienteringen
 * (`ExifInterface.ORIENTATION_*`): 6 = 90°, 3 = 180°, 8 = 270°. Speglade lägen och okänt värde ger 0.
 * Kamerabilder sparas ofta med pixlarna liggande och orienteringen i EXIF, som BitmapFactory ignorerar.
 */
fun exifOrienteringTillGrader(orientering: Int): Int =
    when (orientering) {
        6 -> 90
        3 -> 180
        8 -> 270
        else -> 0
    }

/** Lägger [extra] grader till [grader] och håller resultatet i 0, 90, 180 eller 270. */
fun summeraRotation(grader: Int, extra: Int): Int = ((grader + extra) % 360 + 360) % 360
