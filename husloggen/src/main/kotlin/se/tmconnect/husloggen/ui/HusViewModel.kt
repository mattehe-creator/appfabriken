package se.tmconnect.husloggen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.Uppgift

class HusViewModel(
    repository: HusRepository,
) : ViewModel() {
    val uppgifter: StateFlow<List<Uppgift>> = repository.uppgifter
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val poster: StateFlow<List<Post>> = repository.poster
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
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
