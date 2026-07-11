package com.divarsmartsearch.app.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.divarsmartsearch.app.domain.model.PropertyType
import com.divarsmartsearch.app.domain.usecase.displayLabel
import com.divarsmartsearch.app.presentation.components.AppTextField
import com.divarsmartsearch.app.presentation.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFormScreen(
    viewModel: NewSearchViewModel,
    onContinue: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val draft = state.draft

    if (state.isLoadingForEdit) {
        Scaffold(topBar = { TopAppBar(title = { Text("در حال بارگذاری…") }) }) { padding ->
            com.divarsmartsearch.app.presentation.components.LoadingState(
                modifier = Modifier.padding(padding)
            )
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (state.isEditMode) "ویرایش جستجو" else "جستجوی جدید") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                SectionHeader("اطلاعات پایه")
                AppTextField(
                    value = draft.name,
                    onValueChange = viewModel::updateName,
                    label = "نام جستجو",
                )
            }
            item {
                AppTextField(
                    value = draft.searchUrl,
                    onValueChange = viewModel::updateSearchUrl,
                    label = "لینک جستجوی دیوار",
                    keyboardType = KeyboardType.Uri,
                    supportingText = "لینک صفحه نتایج جستجو را از اپ یا سایت دیوار کپی کنید",
                )
            }

            item {
                SectionHeader("محدوده قیمت", modifier = Modifier.padding(top = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppTextField(
                        value = draft.minPrice,
                        onValueChange = viewModel::updateMinPrice,
                        label = "حداقل قیمت",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                    )
                    AppTextField(
                        value = draft.maxPrice,
                        onValueChange = viewModel::updateMaxPrice,
                        label = "حداکثر قیمت",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                SectionHeader("محدوده متراژ", modifier = Modifier.padding(top = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppTextField(
                        value = draft.minArea,
                        onValueChange = viewModel::updateMinArea,
                        label = "حداقل متراژ",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                    )
                    AppTextField(
                        value = draft.maxArea,
                        onValueChange = viewModel::updateMaxArea,
                        label = "حداکثر متراژ",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                AppTextField(
                    value = draft.maxPricePerMeter,
                    onValueChange = viewModel::updateMaxPricePerMeter,
                    label = "حداکثر قیمت هر متر مربع",
                    keyboardType = KeyboardType.Number,
                )
            }

            item {
                SectionHeader("موقعیت مکانی", modifier = Modifier.padding(top = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppTextField(
                        value = draft.city,
                        onValueChange = viewModel::updateCity,
                        label = "شهر",
                        modifier = Modifier.weight(1f),
                    )
                    AppTextField(
                        value = draft.neighborhood,
                        onValueChange = viewModel::updateNeighborhood,
                        label = "محله",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                SectionHeader("نوع ملک", modifier = Modifier.padding(top = 8.dp))
                PropertyTypeDropdown(
                    selected = draft.propertyType,
                    onSelect = viewModel::updatePropertyType,
                )
            }

            item {
                AppTextField(
                    value = draft.maxListingAgeHours,
                    onValueChange = viewModel::updateMaxListingAgeHours,
                    label = "حداکثر سن آگهی (ساعت)",
                    keyboardType = KeyboardType.Number,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Checkbox(
                        checked = draft.ownersOnly,
                        onCheckedChange = viewModel::updateOwnersOnly,
                    )
                    Text(
                        text = "فقط آگهی‌های مالک",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            }

            state.formError?.let { error ->
                item {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        if (viewModel.validateStepOne()) onContinue()
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                ) {
                    Text("ادامه: فیلترهای دائمی")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PropertyTypeDropdown(
    selected: PropertyType?,
    onSelect: (PropertyType?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        AppTextField(
            value = selected?.displayLabel() ?: "همه انواع",
            onValueChange = {},
            label = "نوع ملک",
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("همه انواع") },
                onClick = { onSelect(null); expanded = false },
            )
            PropertyType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.displayLabel()) },
                    onClick = { onSelect(type); expanded = false },
                )
            }
        }
    }
}
