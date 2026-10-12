package se.tmconnect.garantivalvet

import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** CLAUDE.md punkt 5: appen får inte begära nätverk, inte heller via bibliotek som ML Kit. */
@RunWith(RobolectricTestRunner::class)
class IngenNatverkTest {
    @Test
    fun sammanslagnaManifestetSaknarNatverksbehorigheter() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
        val behorigheter = info.requestedPermissions?.toList().orEmpty()
        assertFalse("INTERNET får inte finnas", "android.permission.INTERNET" in behorigheter)
        assertFalse("ACCESS_NETWORK_STATE får inte finnas", "android.permission.ACCESS_NETWORK_STATE" in behorigheter)
    }
}
