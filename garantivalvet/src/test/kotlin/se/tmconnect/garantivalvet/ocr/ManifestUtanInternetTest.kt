package se.tmconnect.garantivalvet.ocr

import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ManifestUtanInternetTest {
    @Test
    fun sammanslagetManifestHarInteInternet() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        @Suppress("DEPRECATION")
        val packageInfo =
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_PERMISSIONS,
            )
        val permissions = packageInfo.requestedPermissions?.toList().orEmpty()
        assertFalse(
            "Sammanslaget manifest får inte innehålla INTERNET (ML Kit / transport)",
            permissions.contains("android.permission.INTERNET"),
        )
    }
}
