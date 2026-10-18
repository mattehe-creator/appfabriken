package se.tmconnect.rostfangst.data

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import se.tmconnect.rostfangst.regler.Forslag
import se.tmconnect.rostfangst.regler.ForslagTyp
import se.tmconnect.rostfangst.regler.fangsterPerDag

class RostfangstRepository(
    private val dao: RostfangstDao,
    private val zon: ZoneId = ZoneId.of("Europe/Stockholm"),
) {
    val historik: Flow<List<FangstEntitet>> = dao.alla()
    val attGora: Flow<List<FangstEntitet>> = dao.perTyp(ForslagTyp.ATT_GORA)
    val anteckningar: Flow<List<FangstEntitet>> = dao.perTyp(ForslagTyp.ANTECKNING)

    suspend fun sparaInnanTolkning(text: String, nu: Instant = Instant.now()): Long {
        val entitet = FangstEntitet(
            originalText = text,
            typ = ForslagTyp.ANTECKNING,
            titel = text,
            skapadEpochMillis = nu.toEpochMilli(),
            vantarTolkning = true,
        )
        return dao.insert(entitet)
    }

    suspend fun uppdateraEfterTolkning(id: Long, forslag: Forslag, tolkad: Boolean) {
        val befintlig = dao.hamta(id) ?: return
        dao.update(
            befintlig.copy(
                typ = forslag.typ,
                titel = forslag.titel,
                startEpochMillis = forslag.start?.toEpochMilli(),
                slutEpochMillis = forslag.slut?.toEpochMilli(),
                plats = forslag.plats,
                svar = forslag.svar,
                vantarTolkning = !tolkad,
            ),
        )
    }

    suspend fun sparaUtanNat(id: Long) {
        val befintlig = dao.hamta(id) ?: return
        dao.update(
            befintlig.copy(
                typ = ForslagTyp.ANTECKNING,
                titel = befintlig.originalText,
                vantarTolkning = true,
            ),
        )
    }

    suspend fun uppdateraForslag(id: Long, forslag: Forslag) {
        val befintlig = dao.hamta(id) ?: return
        dao.update(
            befintlig.copy(
                typ = forslag.typ,
                titel = forslag.titel,
                startEpochMillis = forslag.start?.toEpochMilli(),
                slutEpochMillis = forslag.slut?.toEpochMilli(),
                plats = forslag.plats,
                svar = forslag.svar,
            ),
        )
    }

    suspend fun markeraKlar(id: Long, klar: Boolean) {
        val befintlig = dao.hamta(id) ?: return
        dao.update(befintlig.copy(klar = klar))
    }

    suspend fun hamta(id: Long): FangstEntitet? = dao.hamta(id)

    suspend fun fangsterPerDagSenaste30(idag: java.time.LocalDate): List<Pair<java.time.LocalDate, Int>> {
        val tider = dao.allaTider().map { Instant.ofEpochMilli(it) }
        return fangsterPerDag(tider, idag, zon)
    }

    fun historikRader(lista: List<FangstEntitet>): List<FangstRad> =
        lista.map { entitet ->
            FangstRad(
                id = entitet.id,
                originalText = entitet.originalText,
                typ = entitet.typ,
                titel = entitet.titel,
                plats = entitet.plats,
                svar = entitet.svar,
                skapadIso = Instant.ofEpochMilli(entitet.skapadEpochMillis)
                    .atZone(zon)
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            )
        }

    suspend fun vantarTolkning(): List<FangstEntitet> = dao.vantarTolkning()
}

fun skapaRostfangstRepository(context: android.content.Context): RostfangstRepository =
    RostfangstRepository(RostfangstDatabas.hamta(context).dao())
