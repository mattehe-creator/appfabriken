package se.tmconnect.rostfangst

import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class NatverkBehorighetTest {
    @Test
    fun manifestHarInternetForTolkning() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
        val behorigheter = info.requestedPermissions?.toList().orEmpty()
        assertTrue("INTERNET krävs för tolkning", "android.permission.INTERNET" in behorigheter)
    }

    @Test
    fun klartextTillatsForHemmanatet() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        // Use a more robust approach to check the flag
        try {
            val appInfo = context.applicationInfo
            val hasCleartextTraffic = appInfo.flags and android.content.pm.ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC != 0
            assertTrue(
                "usesCleartextTraffic krävs för Home Assistant över http",
                hasCleartextTraffic
            )
        } catch (e: Exception) {
            // If direct access fails, check via manifest parsing as fallback
            val manifest = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_META_DATA)
            val usesCleartext = manifest.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC != 0
            assertTrue(
                "usesCleartextTraffic krävs för Home Assistant över http",
                usesCleartext
            )
        }
    }
}
