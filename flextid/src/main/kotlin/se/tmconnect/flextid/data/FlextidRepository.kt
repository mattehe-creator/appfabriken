package se.tmconnect.flextid.data

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import se.tmconnect.flextid.regler.GenvagStamplaInBeslut
import se.tmconnect.flextid.regler.GenvagStamplaUtBeslut
import se.tmconnect.flextid.regler.TidValideringFel
import se.tmconnect.flextid.regler.beraknaSaldo
import se.tmconnect.flextid.regler.beslutGenvagStamplaIn
import se.tmconnect.flextid.regler.beslutGenvagStamplaUt
import se.tmconnect.flextid.regler.dagsForslagVanligDag
import se.tmconnect.flextid.regler.kanLaggaTillDagPost
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.flextid.regler.valideraTider

class FlextidRepository(private val dao: FlextidDao) {
    fun observeraDag(datum: LocalDate): Flow<Dag?> = dao.observeraDag(datum)

    suspend fun hamtaDag(datum: LocalDate): Dag? = dao.hamtaDag(datum)

    fun observeraAllaDagar(): Flow<List<Dag>> = dao.observeraAllaDagar()

    fun observeraDagarMellan(fran: LocalDate, till: LocalDate): Flow<List<Dag>> =
        dao.observeraDagarMellan(fran, till)

    fun observeraInstallning(): Flow<Installning> =
        dao.observeraInstallning().map { it ?: Installning() }

    fun observeraSaldo(): Flow<Int> =
        combine(observeraAllaDagar(), observeraInstallning()) { dagar, inst ->
            beraknaSaldo(dagar, inst)
        }

    fun observeraAntalDagar(): Flow<Int> =
        observeraAllaDagar().map { it.size }

    suspend fun antalDagar(): Int = dao.antalDagar()

    suspend fun hamtaInstallning(): Installning = dao.hamtaInstallning() ?: Installning()

    suspend fun sparaInstallning(installning: Installning) {
        dao.sparaInstallning(installning.copy(id = 1))
    }

    sealed interface SparaDagResult {
        data object Ok : SparaDagResult

        data class Fel(val fel: TidValideringFel) : SparaDagResult

        data object GransUtanPro : SparaDagResult
    }

    sealed interface GenvagStamplaInResult {
        data object Sparat : GenvagStamplaInResult

        data class RedanInstamplad(val start: LocalTime) : GenvagStamplaInResult

        data object GransUtanPro : GenvagStamplaInResult
    }

    sealed interface FyllVanligDagResult {
        data object Sparat : FyllVanligDagResult

        data object FinnsRedan : FyllVanligDagResult

        data object IngetForslag : FyllVanligDagResult

        data object GransUtanPro : FyllVanligDagResult
    }

    sealed interface GenvagStamplaUtResult {
        data object Sparat : GenvagStamplaUtResult

        data object IngenInstempling : GenvagStamplaUtResult

        data object SlutForeStart : GenvagStamplaUtResult

        data object GransUtanPro : GenvagStamplaUtResult
    }

    private suspend fun farSkapaNyDagPost(datum: LocalDate, arPro: Boolean): Boolean {
        if (dao.hamtaDag(datum) != null) return true
        return kanLaggaTillDagPost(dao.antalDagar(), arPro)
    }

    suspend fun sparaDag(dag: Dag, arPro: Boolean): SparaDagResult {
        if (!farSkapaNyDagPost(dag.datum, arPro)) {
            return SparaDagResult.GransUtanPro
        }
        val fel = valideraTider(dag.start, dag.slut)
        if (fel != null) return SparaDagResult.Fel(fel)
        dao.sparaDag(dag)
        return SparaDagResult.Ok
    }

    suspend fun sparaDagUtanValidering(dag: Dag, arPro: Boolean): SparaDagResult {
        if (!farSkapaNyDagPost(dag.datum, arPro)) {
            return SparaDagResult.GransUtanPro
        }
        dao.sparaDag(dag)
        return SparaDagResult.Ok
    }

    suspend fun taBortDag(datum: LocalDate) {
        dao.taBortDag(datum)
    }

    suspend fun hamtaAllaDagar(): List<Dag> = dao.hamtaAllaDagar()

    suspend fun ersattAllData(dagar: List<Dag>, installning: Installning) {
        dao.taBortAllaDagar()
        dagar.forEach { dao.sparaDag(it) }
        dao.sparaInstallning(installning.copy(id = 1))
    }

    suspend fun stamplaIn(datum: LocalDate, tid: java.time.LocalTime, standardRastMin: Int, arPro: Boolean): SparaDagResult {
        val befintlig = dao.hamtaDag(datum)
        if (befintlig == null && !kanLaggaTillDagPost(dao.antalDagar(), arPro)) {
            return SparaDagResult.GransUtanPro
        }
        val dag =
            (befintlig ?: Dag(datum = datum, rastMin = standardRastMin)).copy(
                start = tid,
                typ = befintlig?.typ ?: se.tmconnect.flextid.regler.DagTyp.ARBETE,
            )
        dao.sparaDag(dag)
        return SparaDagResult.Ok
    }

    suspend fun stamplaUt(datum: LocalDate, tid: java.time.LocalTime, arPro: Boolean): SparaDagResult {
        val befintlig = dao.hamtaDag(datum) ?: Dag(datum = datum)
        if (dao.hamtaDag(datum) == null && !kanLaggaTillDagPost(dao.antalDagar(), arPro)) {
            return SparaDagResult.GransUtanPro
        }
        return sparaDag(befintlig.copy(slut = tid), arPro)
    }

    suspend fun fyllVanligDag(
        datum: LocalDate,
        arbetsdagarMask: Int,
        vanligStart: LocalTime,
        vanligSlut: LocalTime,
        vanligRastMin: Int,
        arPro: Boolean,
    ): FyllVanligDagResult {
        if (dao.hamtaDag(datum) != null) return FyllVanligDagResult.FinnsRedan
        val forslag =
            dagsForslagVanligDag(
                datum,
                arbetsdagarMask,
                vanligStart,
                vanligSlut,
                vanligRastMin,
            ) ?: return FyllVanligDagResult.IngetForslag
        val dag =
            Dag(
                datum = datum,
                start = forslag.start,
                slut = forslag.slut,
                rastMin = forslag.rastMin,
                typ = DagTyp.ARBETE,
            )
        return when (sparaDag(dag, arPro)) {
            SparaDagResult.Ok -> FyllVanligDagResult.Sparat
            SparaDagResult.GransUtanPro -> FyllVanligDagResult.GransUtanPro
            is SparaDagResult.Fel -> FyllVanligDagResult.IngetForslag
        }
    }

    suspend fun genvagStamplaIn(datum: LocalDate, nu: LocalTime, standardRastMin: Int, arPro: Boolean): GenvagStamplaInResult {
        val befintlig = dao.hamtaDag(datum)
        return when (val beslut = beslutGenvagStamplaIn(befintlig, nu)) {
            is GenvagStamplaInBeslut.RedanInstamplad ->
                GenvagStamplaInResult.RedanInstamplad(beslut.start)
            is GenvagStamplaInBeslut.SparaStart ->
                when (stamplaIn(datum, beslut.start, standardRastMin, arPro)) {
                    SparaDagResult.Ok -> GenvagStamplaInResult.Sparat
                    SparaDagResult.GransUtanPro -> GenvagStamplaInResult.GransUtanPro
                    is SparaDagResult.Fel -> GenvagStamplaInResult.Sparat
                }
        }
    }

    suspend fun genvagStamplaUt(datum: LocalDate, nu: LocalTime, arPro: Boolean): GenvagStamplaUtResult {
        val befintlig = dao.hamtaDag(datum)
        return when (val beslut = beslutGenvagStamplaUt(befintlig, nu)) {
            GenvagStamplaUtBeslut.IngenInstempling -> GenvagStamplaUtResult.IngenInstempling
            is GenvagStamplaUtBeslut.SparaSlut ->
                when (val sparat = stamplaUt(datum, beslut.slut, arPro)) {
                    SparaDagResult.Ok -> GenvagStamplaUtResult.Sparat
                    SparaDagResult.GransUtanPro -> GenvagStamplaUtResult.GransUtanPro
                    is SparaDagResult.Fel -> GenvagStamplaUtResult.SlutForeStart
                }
        }
    }
}

fun skapaFlextidRepository(context: android.content.Context): FlextidRepository {
    val databas = FlextidDatabas.hamta(context)
    return FlextidRepository(databas.flextidDao())
}
