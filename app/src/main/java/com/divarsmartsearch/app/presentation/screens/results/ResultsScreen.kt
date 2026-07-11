package com.divarsmartsearch.app.presentation.screens.results

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.divarsmartsearch.app.presentation.components.EmptyState
import com.divarsmartsearch.app.presentation.components.ErrorState
import com.divarsmartsearch.app.presentation.components.ListingCard
import com.divarsmartsearch.app.presentation.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    viewModel: ResultsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.blockNumberMessage) {
        state.blockNumberMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearBlockNumberMessage()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("نتایج") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when {
            state.isLoading -> LoadingState(modifier = Modifier.padding(padding))
            state.error != null -> ErrorState(
                message = state.error!!,
                onRetry = viewModel::load,
                modifier = Modifier.padding(padding),
            )
            state.listings.isEmpty() -> EmptyState(
                message = "هنوز آگهی‌ای مطابق فیلترها پیدا نشده",
                modifier = Modifier.padding(padding),
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.listings, key = { it.id }) { listing ->
                    ListingCard(
                        listing = listing,
                        onClick = {
                            viewModel.onOpened(listing.id)
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(listing.url))
                            )
                        },
                        onSave = { viewModel.onSave(listing.id) },
                        onReject = { viewModel.onReject(listing.id) },
                        onBlockPhoneNumber = { viewModel.onBlockPhoneNumber(it) },
                    )
                }
            }
        }
    }
}
