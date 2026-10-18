package se.tmconnect.flextid.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import se.tmconnect.flextid.regler.ARBETSDAGAR_MANDAG_FREDAG

@Entity(tableName = "installning")
data class Installning(
    @PrimaryKey val id: Int = 1,
    val normaltidMinPerDag: Int = 480,
    val standardRastMin: Int = 30,
    val ingangssaldoMin: Int = 0,
    val arbetsdagarMask: Int = ARBETSDAGAR_MANDAG_FREDAG,
)
