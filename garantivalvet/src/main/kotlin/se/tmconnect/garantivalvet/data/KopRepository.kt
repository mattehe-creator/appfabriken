package se.tmconnect.garantivalvet.data

import kotlinx.coroutines.flow.Flow
import se.tmconnect.garantivalvet.export.SakerhetskopiaImportResultat
import se.tmconnect.garantivalvet.export.SakerhetskopiaInnehall
import se.tmconnect.garantivalvet.export.SakerhetskopiaKop
import se.tmconnect.garantivalvet.export.arSakerhetskopiaKopLik
import se.tmconnect.garantivalvet.export.sakerhetskopiaKopTillKop

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

    suspend fun allaKop(): List<Kop> = dao.alla()

    /**
     * Lägger till köp från en läst säkerhetskopia. Hoppar över dubbletter.
     * Saknat kvitto för angivet filnamn → [IllegalStateException] (ingen delvis import).
     */
    suspend fun importeraSakerhetskopia(
        innehall: SakerhetskopiaInnehall,
        kvittoFiler: Map<String, ByteArray>,
    ): SakerhetskopiaImportResultat {
        val befintliga = dao.alla()
        val attLaggaTill = mutableListOf<SakerhetskopiaKop>()
        var hoppadeOver = 0
        val redan = befintliga.map { kop ->
            SakerhetskopiaKop(
                vad = kop.vad,
                varKopt = kop.varKopt,
                kopdatum = kop.kopdatum,
                garantiManader = kop.garantiManader,
                prisOre = kop.prisOre,
            )
        }.toMutableList()

        for (export in innehall.kop) {
            if (redan.any { arSakerhetskopiaKopLik(export, it) }) {
                hoppadeOver++
                continue
            }
            export.kvittoFil?.let { filnamn ->
                if (filnamn !in kvittoFiler) {
                    error("Saknat kvitto i säkerhetskopia: $filnamn")
                }
            }
            attLaggaTill.add(export)
            redan.add(
                SakerhetskopiaKop(
                    vad = export.vad,
                    varKopt = export.varKopt,
                    kopdatum = export.kopdatum,
                    garantiManader = export.garantiManader,
                    prisOre = export.prisOre,
                ),
            )
        }

        val kopAttInfoga =
            attLaggaTill.map { export ->
                val nyttKvitto =
                    export.kvittoFil?.let { filnamn ->
                        kvittoLager.sparaFranBytes(kvittoFiler[filnamn]!!, filnamn)
                    }
                sakerhetskopiaKopTillKop(export, nyttKvitto)
            }
        if (kopAttInfoga.isNotEmpty()) {
            dao.importeraKop(kopAttInfoga)
        }
        return SakerhetskopiaImportResultat(
            tillagda = kopAttInfoga.size,
            hoppadeOver = hoppadeOver,
        )
    }
}
