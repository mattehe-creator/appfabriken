package se.tmconnect.garantivalvet.ui

import androidx.annotation.StringRes
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.regler.Varutyp

@StringRes
fun varutypText(typ: Varutyp): Int =
    when (typ) {
        Varutyp.ELEKTRONIK -> R.string.varutyp_elektronik
        Varutyp.KLADER_SKOR -> R.string.varutyp_klader_skor
        Varutyp.HEM_BYGG -> R.string.varutyp_hem_bygg
        Varutyp.MAT_DRYCK -> R.string.varutyp_mat_dryck
        Varutyp.OVRIGT -> R.string.varutyp_ovrigt
    }
