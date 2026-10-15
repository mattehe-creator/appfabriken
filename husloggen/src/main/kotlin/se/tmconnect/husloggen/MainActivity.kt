package se.tmconnect.husloggen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import se.tmconnect.husloggen.data.HusDatabas
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.husloggen.ui.HusStartSkarm
import se.tmconnect.husloggen.ui.HusViewModel
import se.tmconnect.husloggen.ui.HusViewModelFactory
import se.tmconnect.husloggen.ui.InstallningarSkarm
import se.tmconnect.karna.tema.AppfabrikTema

class MainActivity : ComponentActivity() {
    private val viewModel: HusViewModel by viewModels {
        val databas = HusDatabas.hamta(applicationContext)
        HusViewModelFactory(HusRepository(databas.husDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppfabrikTema(morkt = isSystemInDarkTheme()) {
                var visaInstallningar by rememberSaveable { mutableStateOf(false) }
                val uppgifter by viewModel.uppgifter.collectAsState()
                val poster by viewModel.poster.collectAsState()

                if (visaInstallningar) {
                    InstallningarSkarm(onTillbaka = { visaInstallningar = false })
                } else {
                    HusStartSkarm(
                        uppgifter = uppgifter,
                        poster = poster,
                        onInstallningar = { visaInstallningar = true },
                    )
                }
            }
        }
    }
}
