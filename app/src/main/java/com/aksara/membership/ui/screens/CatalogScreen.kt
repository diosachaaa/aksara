package com.aksara.membership.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksara.membership.data.entity.Product
import com.aksara.membership.ui.components.AksaraTopBar
import com.aksara.membership.ui.viewmodel.MembershipViewModel
import com.aksara.membership.ui.theme.IndigoPrimary
import com.aksara.membership.util.TransactionCategory
import com.aksara.membership.util.toRupiah

@Composable
fun CatalogScreen(
    viewModel: MembershipViewModel,
    onBack: () -> Unit,
    onOpenCart: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val cart by viewModel.cart.collectAsState()
    var category by remember { mutableStateOf(TransactionCategory.BUKU) }

    val shown = remember(products, category) { products.filter { it.category == category.key } }
    val count = cart.values.sum()
    val total = remember(cart, products) {
        products.filter { cart.containsKey(it.id) }.sumOf { it.price * (cart[it.id] ?: 0) }
    }

    Scaffold(
        topBar = { AksaraTopBar("Belanja", onBack = onBack) },
        bottomBar = {
            if (count > 0) {
                Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("$count item", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text(total.toRupiah(), fontWeight = FontWeight.Bold, color = IndigoPrimary)
                        }
                        Button(
                            onClick = onOpenCart,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                        ) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text("Lihat Keranjang")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TransactionCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IndigoPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp, top = 4.dp)
            ) {
                items(shown) { product ->
                    ProductCard(
                        product = product,
                        qty = cart[product.id] ?: 0,
                        onAdd = { viewModel.addToCart(product.id) },
                        onRemove = { viewModel.decrementCart(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductCard(product: Product, qty: Int, onAdd: () -> Unit, onRemove: () -> Unit) {
    val cover = runCatching { Color(android.graphics.Color.parseColor(product.colorHex)) }.getOrDefault(IndigoPrimary)
    val cat = TransactionCategory.fromKey(product.category)

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(96.dp).background(cover),
                contentAlignment = Alignment.Center
            ) {
                Icon(cat.icon, contentDescription = null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(36.dp))
            }
            Column(Modifier.padding(10.dp)) {
                Text(
                    product.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    product.author,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Text(product.price.toRupiah(), color = IndigoPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                if (qty == 0) {
                    Button(
                        onClick = onAdd,
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) { Text("Tambah", fontSize = 13.sp) }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onRemove, modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(IndigoPrimary.copy(alpha = 0.12f))) {
                            Icon(Icons.Filled.Remove, contentDescription = "Kurang", tint = IndigoPrimary, modifier = Modifier.size(18.dp))
                        }
                        Text("$qty", fontWeight = FontWeight.Bold)
                        IconButton(onClick = onAdd, modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(IndigoPrimary)) {
                            Icon(Icons.Filled.Add, contentDescription = "Tambah", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
