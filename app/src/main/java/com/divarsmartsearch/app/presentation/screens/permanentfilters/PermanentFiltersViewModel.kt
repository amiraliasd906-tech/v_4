package com.divarsmartsearch.app.presentation.screens.permanentfilters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.divarsmartsearch.app.domain.model.BlockedPhoneNumber
import com.divarsmartsearch.app.domain.usecase.AddBlockedNumberUseCase
import com.divarsmartsearch.app.domain.usecase.GetBlockedNumbersUseCase
import com.divarsmartsearch.app.domain.usecase.RemoveBlockedNumberUseCase
import com.divarsmartsearch.app.util.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PermanentFiltersUiState(
    val isLoading: Boolean = true,
    val numbers: List<BlockedPhoneNumber> = emptyList(),
    val newPhoneNumber: String = "",
    val newPhoneNote: String = "",
    val addError: String? = null,
    val error: String? = null,
)

@HiltViewModel
class PermanentFiltersViewModel @Inject constructor(
    private val getBlockedNumbersUseCase: GetBlockedNumbersUseCase,
    private val addBlockedNumberUseCase: AddBlockedNumberUseCase,
    private val removeBlockedNumberUseCase: RemoveBlockedNumberUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermanentFiltersUiState())
    val uiState: StateFlow<PermanentFiltersUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getBlockedNumbersUseCase()) {
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, numbers = result.data) }
                is AppResult.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                AppResult.Loading -> Unit
            }
        }
    }

    fun updateNewPhoneNumber(value: String) = _uiState.update { it.copy(newPhoneNumber = value, addError = null) }
    fun updateNewPhoneNote(value: String) = _uiState.update { it.copy(newPhoneNote = value) }

    fun addNumber() {
        val state = _uiState.value
        viewModelScope.launch {
            when (val result = addBlockedNumberUseCase(state.newPhoneNumber, state.newPhoneNote.ifBlank { null })) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        numbers = it.numbers + result.data,
                        newPhoneNumber = "",
                        newPhoneNote = "",
                        addError = null,
                    )
                }
                is AppResult.Error -> _uiState.update { it.copy(addError = result.message) }
                AppResult.Loading -> Unit
            }
        }
    }

    fun removeNumber(id: Int) {
        viewModelScope.launch {
            when (removeBlockedNumberUseCase(id)) {
                is AppResult.Success -> _uiState.update { it.copy(numbers = it.numbers.filterNot { n -> n.id == id }) }
                else -> Unit
            }
        }
    }
}
