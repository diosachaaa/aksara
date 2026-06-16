package com.aksara.membership.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aksara.membership.ui.components.AksaraTopBar
import com.aksara.membership.ui.components.PrimaryButton
import com.aksara.membership.ui.theme.LavenderCard
import com.aksara.membership.ui.theme.IndigoPrimary
import com.aksara.membership.ui.viewmodel.MembershipViewModel
import com.aksara.membership.util.BookCategory
import com.aksara.membership.util.toRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: MembershipViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var bookTitle by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(BookCategory.DEFAULT) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var earnedPoints by remember { mutableStateOf(0) }

    val amount = amountText.toLongOrNull() ?: 0L
    val previewPoint = viewModel.previewPoints(amount)
    val isValid = amount > 0 && bookTitle.isNotBlank()

    Scaffold(topBar = { AksaraTopBar("Add Transaction", onBack = onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // ----- Judul Buku -----
            Text("Judul Buku", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = bookTitle,
                onValueChange = { bookTitle = it },
                placeholder = { Text("Laskar Pelangi") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // ----- Kategori Buku (dropdown) -----
            Text("Kategori", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    BookCategory.ALL.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                category = option
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ----- Nominal Pembelian -----
            Text("Nominal Pembelian", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { input -> amountText = input.filter { it.isDigit() } },
                placeholder = { Text("150000") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            if (amount > 0) {
                Text(
                    amount.toRupiah(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(Modifier.height(20.dp))
            Text("Point Didapat", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LavenderCard)
            ) {
                Text(
                    "$previewPoint",
                    style = MaterialTheme.typography.headlineMedium,
                    color = IndigoPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = IndigoPrimary)
                Spacer(Modifier.width(6.dp))
                Text("1 Poin = Rp10.000", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(32.dp))
            PrimaryButton(text = "Simpan", enabled = isValid) {
                viewModel.addTransaction(bookTitle, category, amount) { points ->
                    earnedPoints = points
                    showSuccess = true
                }
            }
        }
    }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = IndigoPrimary
                )
            },
            title = { Text("Transaksi Berhasil!", fontWeight = FontWeight.Bold) },
            text = { Text("Anda mendapatkan +$earnedPoints poin. Poin telah tersimpan.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccess = false
                    onSaved()
                }) { Text("OK") }
            }
        )
    }
}
