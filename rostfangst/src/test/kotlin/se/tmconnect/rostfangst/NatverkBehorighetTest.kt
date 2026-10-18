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
}
