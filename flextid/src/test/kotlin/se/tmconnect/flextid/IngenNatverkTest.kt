package se.tmconnect.flextid

import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class IngenNatverkTest {
    @Test
    fun sammanslagnaManifestetSaknarNatverksbehorigheter() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
        val behorigheter = info.requestedPermissions?.toList().orEmpty()
        assertFalse("INTERNET får inte finnas", "android.permission.INTERNET" in behorigheter)
        assertFalse(
            "ACCESS_NETWORK_STATE får inte finnas",
            "android.permission.ACCESS_NETWORK_STATE" in behorigheter,
        )
    }
}
