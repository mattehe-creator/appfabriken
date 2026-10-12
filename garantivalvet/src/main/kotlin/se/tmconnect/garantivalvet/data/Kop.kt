package se.tmconnect.garantivalvet.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "kop")
data class Kop(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vad: String,
    @ColumnInfo(name = "var_kopt")
    val varKopt: String? = null,
    val kopdatum: LocalDate,
    @ColumnInfo(name = "garanti_manader")
    val garantiManader: Int,
    @ColumnInfo(name = "pris_ore")
    val prisOre: Long? = null,
    val anteckning: String? = null,
    @ColumnInfo(name = "kvitto_fil")
    val kvittoFil: String? = null,
    val skapad: Long = System.currentTimeMillis(),
    /** Butikens öppna köp i dagar från köpdatum, `null` om inget. Frivilligt erbjudande, inte lag. */
    @ColumnInfo(name = "oppet_kop_dagar")
    val oppetKopDagar: Int? = null,
    /** Namnet på en [se.tmconnect.garantivalvet.regler.Varutyp], `null` om användaren inte angett. */
    val varutyp: String? = null,
    /** Dagen användaren upptäckte ett fel, `null` om inget fel angetts. */
    @ColumnInfo(name = "fel_upptackt")
    val felUpptackt: LocalDate? = null,
    @ColumnInfo(name = "fel_beskrivning")
    val felBeskrivning: String? = null,
    /** Dagen användaren reklamerade hos företaget, `null` om inte gjort. */
    val reklamerad: LocalDate? = null,
    /** Butikens kontaktuppgifter, till nytta när man ska reklamera. Personnummer sparas aldrig. */
    @ColumnInfo(name = "butik_epost")
    val butikEpost: String? = null,
    @ColumnInfo(name = "butik_telefon")
    val butikTelefon: String? = null,
    @ColumnInfo(name = "butik_orgnr")
    val butikOrgnr: String? = null,
)
