package com.aksara.membership.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksara.membership.ui.components.AksaraTopBar
import com.aksara.membership.ui.components.EmptyState
import com.aksara.membership.ui.components.PrimaryButton
import com.aksara.membership.ui.theme.IndigoPrimary
import com.aksara.membership.ui.viewmodel.MembershipViewModel
import com.aksara.membership.util.PointCalculator
import com.aksara.membership.util.TransactionCategory
import com.aksara.membership.util.productImageRes
import com.aksara.membership.util.toRupiah

@Composable
fun CartScreen(
    viewModel: MembershipViewModel,
    onBack: () -> Unit,
    onPaid: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val cart by viewModel.cart.collectAsState()

    val lines = products.filter { cart.containsKey(it.id) }.map { it to (cart[it.id] ?: 0) }
    val total = lines.sumOf { it.first.price * it.second }
    val points = PointCalculator.calculate(total)

    Scaffold(
        topBar = { AksaraTopBar("Keranjang", onBack = onBack) },
        bottomBar = {
            if (lines.isNotEmpty()) {
                Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total")
                            Text(total.toRupiah(), fontWeight = FontWeight.Bold)
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Poin didapat", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            Text("+$points poin", color = IndigoPrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(12.dp))
                        PrimaryButton(text = "Bayar Sekarang") {
                            viewModel.checkout { onPaid() }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (lines.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                EmptyState(Icons.Filled.ShoppingCart, "Keranjang masih kosong.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(lines) { (product, qty) ->
                    val cover = runCatching { Color(android.graphics.Color.parseColor(product.colorHex)) }.getOrDefault(IndigoPrimary)
                    val cat = TransactionCategory.fromKey(product.category)
                    val imageRes = productImageRes(product.imageKey)
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)).background(cover),
                                contentAlignment = Alignment.Center
                            ) {
                                if (imageRes != null) {
                                    Image(
                                        painter = painterResource(imageRes),
                                        contentDescription = product.name,
                                        modifier = Modifier.size(34.dp)
                                    )
                                } else {
                                    Icon(cat.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                                }
                            }
                            Spacer(Modifier.size(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(product.name, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                Text(product.price.toRupiah(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }
                            IconButton(onClick = { viewModel.decrementCart(product.id) }, modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(IndigoPrimary.copy(alpha = 0.12f))) {
                                Icon(Icons.Filled.Remove, contentDescription = "Kurang", tint = IndigoPrimary, modifier = Modifier.size(16.dp))
                            }
                            Text("  $qty  ", fontWeight = FontWeight.Bold)
                            IconButton(onClick = { viewModel.addToCart(product.id) }, modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(IndigoPrimary)) {
                                Icon(Icons.Filled.Add, contentDescription = "Tambah", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = { viewModel.removeFromCart(product.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
