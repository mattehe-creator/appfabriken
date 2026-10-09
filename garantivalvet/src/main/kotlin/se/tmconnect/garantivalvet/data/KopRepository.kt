package se.tmconnect.garantivalvet.data

import kotlinx.coroutines.flow.Flow

class KopRepository(
    private val dao: KopDao,
    private val kvittoLager: KvittoLager,
) {
    val alla: Flow<List<Kop>> = dao.allaFlow()

    suspend fun hamta(id: Long): Kop? = dao.hamta(id)

    suspend fun spara(kop: Kop): Long =
        if (kop.id == 0L) {
            dao.infoga(kop)
        } else {
            dao.uppdatera(kop)
            kop.id
        }

    suspend fun taBort(kop: Kop) {
        kvittoLager.taBort(kop.kvittoFil)
        dao.taBort(kop)
    }

    suspend fun antal(): Int = dao.antal()
}
