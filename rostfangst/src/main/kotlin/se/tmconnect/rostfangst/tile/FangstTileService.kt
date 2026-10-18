package se.tmconnect.rostfangst.tile

import android.content.Intent
import android.service.quicksettings.TileService
import se.tmconnect.rostfangst.MainActivity

class FangstTileService : TileService() {
    override fun onClick() {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = MainActivity.ACTION_SAG_DET
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(MainActivity.EXTRA_START_INSPELNING, true)
        }
        unlockAndRun {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
