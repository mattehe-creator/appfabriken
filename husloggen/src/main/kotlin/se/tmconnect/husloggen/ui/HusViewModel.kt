package se.tmconnect.husloggen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.UppgiftFel
import se.tmconnect.husloggen.regler.sorteraUppgifter
import se.tmconnect.husloggen.regler.valideraUppgift

data class UppgiftFormularTillstand(
    val redigerarId: Long = 0L,
    val namn: String = "",
    val intervallText: String = "",
    val startDatum: LocalDate = LocalDate.now(),
    val paminnelse: Boolean = true,
    val anteckning: String = "",
    val valideringsFel: UppgiftFel? = null,
)

sealed class HusUiMeddelande {
    data object UppgiftMarkeradGjord : HusUiMeddelande()
}

class HusViewModel(
    private val repository: HusRepository,
    private val idag: LocalDate = LocalDate.now(),
) : ViewModel() {
    val uppgifter: StateFlow<List<Uppgift>> = repository.uppgifter
        .map { sorteraUppgifter(it, idag) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val poster: StateFlow<List<Post>> = repository.poster
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _formular = MutableStateFlow<UppgiftFormularTillstand?>(null)
    val formular: StateFlow<UppgiftFormularTillstand?> = _formular.asStateFlow()

    private val _meddelanden = MutableSharedFlow<HusUiMeddelande>()
    val meddelanden: SharedFlow<HusUiMeddelande> = _meddelanden.asSharedFlow()

    fun visaNyUppgift() {
        _formular.value = UppgiftFormularTillstand(startDatum = idag)
    }

    fun visaAndraUppgift(uppgiftId: Long) {
        viewModelScope.launch {
            val uppgift = repository.hamtaUppgift(uppgiftId) ?: return@launch
            _formular.value = UppgiftFormularTillstand(
                redigerarId = uppgift.id,
                namn = uppgift.namn,
                intervallText = uppgift.intervallManader.toString(),
                startDatum = uppgift.startDatum,
                paminnelse = uppgift.paminnelse,
                anteckning = uppgift.anteckning.orEmpty(),
            )
        }
    }

    fun stangFormular() {
        _formular.value = null
    }

    fun uppdateraFormularNamn(namn: String) {
        uppdateraFormular { it.copy(namn = namn, valideringsFel = null) }
    }

    fun uppdateraFormularIntervall(text: String) {
        uppdateraFormular { it.copy(intervallText = text, valideringsFel = null) }
    }

    fun uppdateraFormularStartdatum(datum: LocalDate) {
        uppdateraFormular { it.copy(startDatum = datum) }
    }

    fun uppdateraFormularPaminnelse(pa: Boolean) {
        uppdateraFormular { it.copy(paminnelse = pa) }
    }

    fun uppdateraFormularAnteckning(text: String) {
        uppdateraFormular { it.copy(anteckning = text) }
    }

    fun appliceraForslag(namn: String, intervallManader: Int) {
        uppdateraFormular {
            it.copy(
                namn = namn,
                intervallText = intervallManader.toString(),
                valideringsFel = null,
            )
        }
    }

    fun sparaFormular(): Boolean {
        val tillstand = _formular.value ?: return false
        val intervall = tillstand.intervallText.trim().toIntOrNull()
        val fel = valideraUppgift(tillstand.namn, intervall)
        if (fel != null) {
            _formular.value = tillstand.copy(valideringsFel = fel)
            return false
        }
        viewModelScope.launch {
            val befintlig = if (tillstand.redigerarId != 0L) {
                repository.hamtaUppgift(tillstand.redigerarId)
            } else {
                null
            }
            val uppgift = Uppgift(
                id = tillstand.redigerarId,
                namn = tillstand.namn.trim(),
                intervallManader = intervall!!,
                startDatum = tillstand.startDatum,
                senastGjord = befintlig?.senastGjord,
                paminnelse = tillstand.paminnelse,
                anteckning = tillstand.anteckning.trim().ifEmpty { null },
                skapad = befintlig?.skapad ?: System.currentTimeMillis(),
            )
            repository.sparaUppgift(uppgift)
            _formular.value = null
        }
        return true
    }

    fun taBortFormularUppgift() {
        val tillstand = _formular.value ?: return
        if (tillstand.redigerarId == 0L) return
        viewModelScope.launch {
            repository.hamtaUppgift(tillstand.redigerarId)?.let { repository.taBortUppgift(it) }
            _formular.value = null
        }
    }

    fun markeraSomGjord(uppgiftId: Long) {
        viewModelScope.launch {
            val uppgift = repository.hamtaUppgift(uppgiftId) ?: return@launch
            repository.markeraSomGjord(uppgift, idag)
            _meddelanden.emit(HusUiMeddelande.UppgiftMarkeradGjord)
        }
    }

    private fun uppdateraFormular(block: (UppgiftFormularTillstand) -> UppgiftFormularTillstand) {
        _formular.value = _formular.value?.let(block)
    }
}

class HusViewModelFactory(
    private val repository: HusRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HusViewModel::class.java)) {
            return HusViewModel(repository) as T
        }
        throw IllegalArgumentException("Okänd ViewModel: ${modelClass.name}")
    }
}
