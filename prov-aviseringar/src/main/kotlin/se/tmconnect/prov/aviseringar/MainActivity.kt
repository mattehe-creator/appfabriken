package se.tmconnect.prov.aviseringar

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import se.tmconnect.karna.tema.AppfabrikTema
import se.tmconnect.prov.aviseringar.export.aviseringExportFilnamn
import se.tmconnect.prov.aviseringar.ui.AviseringAppSkarm
import se.tmconnect.prov.aviseringar.ui.AviseringSkarm
import se.tmconnect.prov.aviseringar.ui.AviseringViewModel
import se.tmconnect.prov.aviseringar.ui.AviseringViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: AviseringViewModel by viewModels {
        AviseringViewModelFactory(
            (application as ProvAviseringarApp).repository,
            applicationContext,
        )
    }

    private val skapaJsonContract =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            if (uri != null) {
                viewModel.skrivTillUri(
                    uri = uri,
                    skriv = { out ->
                        val json = viewModel.skapaExportJson()
                        out.write(json.toByteArray(Charsets.UTF_8))
                    },
                    onResultat = { ok ->
                        val text =
                            if (ok) {
                                getString(R.string.export_klar)
                            } else {
                                getString(R.string.export_misslyckades)
                            }
                        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                    },
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppfabrikTema {
                var skarm by rememberSaveable { mutableStateOf(AviseringSkarm.HEM) }
                val atkomst by viewModel.atkomstAktiverad.collectAsStateWithLifecycle()
                val senaste by viewModel.senaste.collectAsStateWithLifecycle()
                val antalPerApp by viewModel.antalPerApp.collectAsStateWithLifecycle()
                val paketStatistik by viewModel.paketStatistik.collectAsStateWithLifecycle()
                val valdaPaket by viewModel.valdaPaket.collectAsStateWithLifecycle()

                AviseringAppSkarm(
                    skarm = skarm,
                    atkomstAktiverad = atkomst,
                    antalPerApp = antalPerApp,
                    senaste = senaste,
                    paketStatistik = paketStatistik,
                    valdaPaket = valdaPaket,
                    onSkarm = { skarm = it },
                    onOppnaAtkomst = {
                        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    },
                    onExportera = { skapaJsonContract.launch(aviseringExportFilnamn()) },
                    onRensa = {
                        viewModel.rensaLogg {
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.rensa_klar),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    },
                    onValtPaket = viewModel::uppdateraValtPaket,
                )
            }
        }
    }
}
