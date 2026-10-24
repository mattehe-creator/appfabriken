package se.tmconnect.flextid.export

import android.content.Context
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import se.tmconnect.flextid.R
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.flextid.regler.ManadsRapportTexter

fun manadsRapportTexter(
    context: Context,
    arManad: YearMonth,
    locale: Locale,
): ManadsRapportTexter {
    val formatter = DateTimeFormatter.ofPattern("LLLL yyyy", locale)
    return ManadsRapportTexter(
        manadArRubrik = arManad.atDay(1).format(formatter),
        ingaRegistreradeDagar = context.getString(R.string.pdf_inga_dagar),
        summaArbetadEtikett = context.getString(R.string.pdf_summa_arbetad),
        summaUtfallEtikett = context.getString(R.string.pdf_summa_utfall),
        saldoManadsslutEtikett = context.getString(R.string.pdf_saldo_manadsslut),
        kolumnRubriker =
            listOf(
                context.getString(R.string.pdf_kolumn_datum),
                context.getString(R.string.pdf_kolumn_veckodag),
                context.getString(R.string.pdf_kolumn_start),
                context.getString(R.string.pdf_kolumn_slut),
                context.getString(R.string.pdf_kolumn_rast),
                context.getString(R.string.pdf_kolumn_arbetad),
                context.getString(R.string.pdf_kolumn_utfall),
                context.getString(R.string.pdf_kolumn_typ),
            ),
        typEtiketter =
            DagTyp.entries.associateWith { typ ->
                context.getString(dagTypStringRes(typ))
            },
        veckodagEtiketter =
            DayOfWeek.entries.associateWith { dag ->
                context.getString(veckodagStringRes(dag))
            },
        saknasTecken = context.getString(R.string.pdf_saknas),
    )
}

private fun dagTypStringRes(typ: DagTyp): Int =
    when (typ) {
        DagTyp.ARBETE -> R.string.dagtyp_arbete
        DagTyp.FLEX -> R.string.dagtyp_flex
        DagTyp.LEDIG -> R.string.dagtyp_ledig
        DagTyp.SEMESTER -> R.string.dagtyp_semester
        DagTyp.SJUK -> R.string.dagtyp_sjuk
        DagTyp.HELGDAG -> R.string.dagtyp_helgdag
    }

private fun veckodagStringRes(dag: DayOfWeek): Int =
    when (dag) {
        DayOfWeek.MONDAY -> R.string.veckodag_man
        DayOfWeek.TUESDAY -> R.string.veckodag_tis
        DayOfWeek.WEDNESDAY -> R.string.veckodag_ons
        DayOfWeek.THURSDAY -> R.string.veckodag_tor
        DayOfWeek.FRIDAY -> R.string.veckodag_fre
        DayOfWeek.SATURDAY -> R.string.veckodag_lor
        DayOfWeek.SUNDAY -> R.string.veckodag_son
    }

fun normaltidManadEtikett(context: Context, arbetsdagar: Int): String =
    context.getString(R.string.pdf_normaltid_manad, arbetsdagar)
