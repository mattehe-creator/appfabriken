package se.tmconnect.garantivalvet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.data.KopRepository
import se.tmconnect.garantivalvet.regler.sorteraEfterGaranti

class KopViewModel(
    private val repository: KopRepository,
    private val idag: LocalDate = LocalDate.now(),
) : ViewModel() {
    val kopLista: StateFlow<List<Kop>> =
        repository.alla
            .map { lista -> sorteraEfterGaranti(lista, idag) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    private val _aktivSkarm = MutableStateFlow<KopSkarm>(KopSkarm.Lista)
    val aktivSkarm: StateFlow<KopSkarm> = _aktivSkarm.asStateFlow()

    private val _valtKop = MutableStateFlow<Kop?>(null)
    val valtKop: StateFlow<Kop?> = _valtKop.asStateFlow()

    fun visaLista() {
        _aktivSkarm.value = KopSkarm.Lista
        _valtKop.value = null
    }

    fun visaLaggTill() {
        _aktivSkarm.value = KopSkarm.LaggTill
        _valtKop.value = null
    }

    fun visaDetalj(kopId: Long) {
        viewModelScope.launch {
            _valtKop.value = repository.hamta(kopId)
            _aktivSkarm.value = KopSkarm.Detalj(kopId)
        }
    }

    fun visaAndra(kopId: Long) {
        viewModelScope.launch {
            _valtKop.value = repository.hamta(kopId)
            _aktivSkarm.value = KopSkarm.Andra(kopId)
        }
    }

    fun sparaNyttKop(
        vad: String,
        varKopt: String?,
        kopdatum: LocalDate,
        garantiManader: Int,
        prisOre: Long?,
        anteckning: String?,
    ) {
        viewModelScope.launch {
            repository.spara(
                Kop(
                    vad = vad.trim(),
                    varKopt = varKopt?.trim()?.takeIf { it.isNotEmpty() },
                    kopdatum = kopdatum,
                    garantiManader = garantiManader,
                    prisOre = prisOre,
                    anteckning = anteckning?.trim()?.takeIf { it.isNotEmpty() },
                ),
            )
            visaLista()
        }
    }

    fun uppdateraKop(kop: Kop) {
        viewModelScope.launch {
            repository.spara(kop)
            visaLista()
        }
    }

    fun taBortValtKop() {
        val kop = _valtKop.value ?: return
        viewModelScope.launch {
            repository.taBort(kop)
            visaLista()
        }
    }
}

class KopViewModelFactory(
    private val repository: KopRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KopViewModel::class.java)) {
            return KopViewModel(repository) as T
        }
        throw IllegalArgumentException("Okänd ViewModel: ${modelClass.name}")
    }
}
