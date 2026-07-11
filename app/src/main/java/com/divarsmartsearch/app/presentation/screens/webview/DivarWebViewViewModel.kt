package com.divarsmartsearch.app.presentation.screens.webview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.divarsmartsearch.app.data.webview.ExtractedListing
import com.divarsmartsearch.app.data.webview.ListingIngestionService
import com.divarsmartsearch.app.domain.usecase.GetSearchByIdUseCase
import com.divarsmartsearch.app.util.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class DivarWebViewUiState(
    val isLoadingSearch: Boolean = true,
    val searchName: String = "",
    val startUrl: String = "https://divar.ir",
    val error: String? = null,
    val statusMessage: String? = null,
    val totalPassed: Int = 0,
)

@HiltViewModel
class DivarWebViewViewModel @Inject constructor(
    private val getSearchByIdUseCase: GetSearchByIdUseCase,
    private val ingestionService: ListingIngestionService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DivarWebViewUiState())
    val uiState: StateFlow<DivarWebViewUiState> = _uiState.asStateFlow()

    private var savedSearchId: Long = 0
    private val json = Json { ignoreUnknownKeys = true }

    fun load(searchId: Int) {
        savedSearchId = searchId.toLong()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSearch = true) }
            when (val result = getSearchByIdUseCase(searchId)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(
                        isLoadingSearch = false,
                        searchName = result.data.name,
                        startUrl = result.data.searchUrl,
                    )
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(isLoadingSearch = false, error = result.message)
                }
                AppResult.Loading -> Unit
            }
        }
    }

    /** Called from the JS bridge whenever the injected script finds listings on the current page. */
    fun onListingsExtracted(rawJson: String) {
        viewModelScope.launch {
            try {
                val listings = json.decodeFromString<List<ExtractedListing>>(rawJson)
                if (listings.isEmpty()) return@launch
                val result = ingestionService.ingest(savedSearchId, listings)
                if (result.passedFilters > 0) {
                    _uiState.update {
                        it.copy(
                            statusMessage = "${result.passedFilters} آگهی جدید پیدا و فیلتر شد",
                            totalPassed = it.totalPassed + result.passedFilters,
                        )
                    }
                }
            } catch (e: Exception) {
                // Malformed JSON from the page — ignore this batch silently,
                // extraction will simply try again on the next cycle.
            }
        }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }
}
