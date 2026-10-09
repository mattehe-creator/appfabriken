package se.tmconnect.garantivalvet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import java.time.LocalDate
import se.tmconnect.garantivalvet.data.GarantiDatabas
import se.tmconnect.garantivalvet.data.KopRepository
import se.tmconnect.garantivalvet.ui.KopDetaljSkarm
import se.tmconnect.garantivalvet.ui.KopFormularSkarm
import se.tmconnect.garantivalvet.ui.KopListaSkarm
import se.tmconnect.garantivalvet.ui.KopSkarm
import se.tmconnect.garantivalvet.ui.KopViewModel
import se.tmconnect.garantivalvet.ui.KopViewModelFactory
import se.tmconnect.karna.tema.AppfabrikTema

class MainActivity : ComponentActivity() {
    private val viewModel: KopViewModel by viewModels {
        val databas = GarantiDatabas.hamta(applicationContext)
        KopViewModelFactory(KopRepository(databas.kopDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppfabrikTema {
                val kopLista by viewModel.kopLista.collectAsState()
                val aktivSkarm by viewModel.aktivSkarm.collectAsState()
                val valtKop by viewModel.valtKop.collectAsState()

                when (aktivSkarm) {
                    KopSkarm.Lista -> {
                        KopListaSkarm(
                            kopLista = kopLista,
                            onLaggTill = viewModel::visaLaggTill,
                            onOppna = viewModel::visaDetalj,
                        )
                    }

                    is KopSkarm.Detalj -> {
                        val kop = valtKop
                        if (kop != null) {
                            KopDetaljSkarm(
                                kop = kop,
                                onAndra = { viewModel.visaAndra(kop.id) },
                                onTaBort = viewModel::taBortValtKop,
                                onTillbaka = viewModel::visaLista,
                            )
                        }
                    }

                    KopSkarm.LaggTill -> {
                        KopFormularSkarm(
                            titel = stringResource(R.string.lagg_till_titel),
                            initialVad = "",
                            initialVar = "",
                            initialGarantiManader = "24",
                            initialPris = null,
                            initialAnteckning = null,
                            initialKopdatum = LocalDate.now(),
                            onSpara = { vad, varKopt, kopdatum, garantiManader, prisOre, anteckning ->
                                viewModel.sparaNyttKop(
                                    vad = vad,
                                    varKopt = varKopt,
                                    kopdatum = kopdatum,
                                    garantiManader = garantiManader,
                                    prisOre = prisOre,
                                    anteckning = anteckning,
                                )
                            },
                            onAvbryt = viewModel::visaLista,
                        )
                    }

                    is KopSkarm.Andra -> {
                        val kop = valtKop
                        if (kop != null) {
                            KopFormularSkarm(
                                titel = stringResource(R.string.andra_titel),
                                initialVad = kop.vad,
                                initialVar = kop.varKopt.orEmpty(),
                                initialGarantiManader = kop.garantiManader.toString(),
                                initialPris = kop.prisOre?.let { (it / 100.0).toString() },
                                initialAnteckning = kop.anteckning,
                                initialKopdatum = kop.kopdatum,
                                onSpara = { vad, varKopt, kopdatum, garantiManader, prisOre, anteckning ->
                                    viewModel.uppdateraKop(
                                        kop.copy(
                                            vad = vad.trim(),
                                            varKopt = varKopt?.trim()?.takeIf { it.isNotEmpty() },
                                            kopdatum = kopdatum,
                                            garantiManader = garantiManader,
                                            prisOre = prisOre,
                                            anteckning = anteckning?.trim()?.takeIf { it.isNotEmpty() },
                                        ),
                                    )
                                },
                                onAvbryt = viewModel::visaLista,
                            )
                        }
                    }
                }
            }
        }
    }
}
