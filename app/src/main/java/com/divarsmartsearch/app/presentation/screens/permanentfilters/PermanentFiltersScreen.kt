package com.divarsmartsearch.app.presentation.screens.permanentfilters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.divarsmartsearch.app.presentation.components.AppTextField
import com.divarsmartsearch.app.presentation.components.EmptyState
import com.divarsmartsearch.app.presentation.components.LoadingState
import com.divarsmartsearch.app.presentation.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermanentFiltersScreen(
    viewModel: PermanentFiltersViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("فیلترهای دائمی") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    "شماره‌هایی که هرگز نمی‌خواهید آگهی‌شان نمایش داده شود",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item {
                SectionHeader("افزودن شماره جدید", modifier = Modifier.padding(top = 8.dp))
                AppTextField(
                    value = state.newPhoneNumber,
                    onValueChange = viewModel::updateNewPhoneNumber,
                    label = "شماره تلفن",
                    keyboardType = KeyboardType.Phone,
                    isError = state.addError != null,
                    supportingText = state.addError,
                )
            }
            item {
                AppTextField(
                    value = state.newPhoneNote,
                    onValueChange = viewModel::updateNewPhoneNote,
                    label = "یادداشت (اختیاری)",
                )
            }
            item {
                OutlinedButton(onClick = viewModel::addNumber, modifier = Modifier.fillMaxWidth()) {
                    Text("افزودن به لیست")
                }
            }

            if (state.isLoading) {
                item { LoadingState() }
            } else if (state.numbers.isEmpty()) {
                item { EmptyState("هیچ شماره‌ای مسدود نشده است") }
            } else {
                item { SectionHeader("لیست فعلی", modifier = Modifier.padding(top = 16.dp)) }
                items(state.numbers, key = { it.id }) { blocked ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text(blocked.phoneNumber, style = MaterialTheme.typography.bodyLarge)
                                blocked.note?.let {
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            IconButton(onClick = { viewModel.removeNumber(blocked.id) }) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "حذف",
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
