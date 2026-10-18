package se.tmconnect.prov.aviseringar.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.io.OutputStream
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import se.tmconnect.prov.aviseringar.data.AviseringRepository
import se.tmconnect.prov.aviseringar.export.skrivAviseringExportJson
import se.tmconnect.prov.aviseringar.util.arAviseringsAtkomstAktiverad

class AviseringViewModel(
    private val repository: AviseringRepository,
    private val appContext: Context,
) : ViewModel() {
    val atkomstAktiverad =
        kotlinx.coroutines.flow.flow {
            while (true) {
                emit(arAviseringsAtkomstAktiverad(appContext))
                kotlinx.coroutines.delay(1_000)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val senaste = repository.senasteAviseringar.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val antalPerApp = repository.antalPerApp.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val paketStatistik =
        repository.paketStatistik.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val valdaPaket = repository.valdaPaket

    fun uppdateraValtPaket(paketNamn: String, valt: Boolean) {
        viewModelScope.launch {
            repository.uppdateraValtPaket(paketNamn, valt)
        }
    }

    fun rensaLogg(onKlar: () -> Unit) {
        viewModelScope.launch {
            repository.rensaLogg()
            onKlar()
        }
    }

    suspend fun skapaExportJson(): String {
        val rader = repository.hamtaAllaForExport()
        return skrivAviseringExportJson(rader)
    }

    fun skrivTillUri(uri: Uri, skriv: suspend (OutputStream) -> Unit, onResultat: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok =
                runCatching {
                    withContext(Dispatchers.IO) {
                        appContext.contentResolver.openOutputStream(uri)?.use { out ->
                            skriv(out)
                        } ?: error("Ingen OutputStream")
                    }
                }.isSuccess
            onResultat(ok)
        }
    }
}

class AviseringViewModelFactory(
    private val repository: AviseringRepository,
    private val appContext: Context,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AviseringViewModel::class.java)) {
            return AviseringViewModel(repository, appContext) as T
        }
        throw IllegalArgumentException("Okänd ViewModel: ${modelClass.name}")
    }
}
