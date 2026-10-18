package se.tmconnect.flextid.data

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import se.tmconnect.flextid.regler.TidValideringFel
import se.tmconnect.flextid.regler.beraknaSaldo
import se.tmconnect.flextid.regler.kanLaggaTillDagPost
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
}

fun skapaFlextidRepository(context: android.content.Context): FlextidRepository {
    val databas = FlextidDatabas.hamta(context)
    return FlextidRepository(databas.flextidDao())
}
