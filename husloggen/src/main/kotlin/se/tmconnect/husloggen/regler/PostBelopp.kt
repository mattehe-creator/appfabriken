package se.tmconnect.husloggen.regler

import se.tmconnect.husloggen.data.Post

/** Belopp i öre som räknas i sammanställning, PDF och CSV (fakturabelopp minus rot/rut). */
fun postBeloppForSammanstallningOre(post: Post): Long? {
    val brutto = post.beloppOre ?: return null
    val avdrag = post.rotRutAvdragOre ?: 0L
    return brutto - avdrag
}
