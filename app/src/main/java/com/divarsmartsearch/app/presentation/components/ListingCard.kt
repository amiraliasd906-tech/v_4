package com.divarsmartsearch.app.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.divarsmartsearch.app.domain.model.Listing
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ListingCard(
    listing: Listing,
    onClick: () -> Unit,
    onSave: () -> Unit,
    onReject: () -> Unit,
    onBlockPhoneNumber: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = listing.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = listing.price?.let { formatToman(it) } ?: "قیمت نامشخص",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                listing.area?.let {
                    Text(
                        text = "${it.toInt()} متر",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            listing.pricePerMeter?.let {
                Text(
                    text = "${formatToman(it)} / متر",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            if (!listing.neighborhood.isNullOrBlank()) {
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(end = 4.dp),
                    )
                    Text(
                        text = listing.neighborhood,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (listing.detectedPhoneNumbers.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "شماره‌های یافت‌شده در متن آگهی:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    listing.detectedPhoneNumbers.forEach { phoneNumber ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        ) {
                            Text(
                                text = phoneNumber,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            androidx.compose.material3.TextButton(
                                onClick = { onBlockPhoneNumber(phoneNumber) },
                            ) {
                                Text(
                                    "مسدود کردن",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = onReject) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "رد کردن",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
                IconButton(onClick = onSave) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = "ذخیره",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

private fun formatToman(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale("fa", "IR"))
    return "${formatter.format(amount.toLong())} تومان"
}
