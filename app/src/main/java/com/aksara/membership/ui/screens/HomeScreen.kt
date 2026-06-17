package com.aksara.membership.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksara.membership.ui.components.AvatarImage
import com.aksara.membership.ui.components.TierBadge
import com.aksara.membership.ui.components.brandBrush
import com.aksara.membership.ui.theme.GoldAccent
import com.aksara.membership.ui.theme.IndigoPrimary
import com.aksara.membership.ui.viewmodel.MembershipViewModel
import com.aksara.membership.util.MemberTier
import com.aksara.membership.util.TransactionCategory
import com.aksara.membership.util.toIndoDate
import com.aksara.membership.util.toRupiah

@Composable
fun HomeScreen(
    viewModel: MembershipViewModel,
    onOpenCard: () -> Unit,
    onOpenTransactions: () -> Unit,
    onOpenRewards: () -> Unit,
    onAddTransaction: () -> Unit
) {
    val member by viewModel.member.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    val points = member?.totalPoints ?: 0
    val tier = MemberTier.of(points)
    val toNext = MemberTier.pointsToNext(points)
    val progress = MemberTier.progress(points)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // ---------- HEADER GRADIENT ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(brandBrush())
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Halo,", color = Color.White.copy(alpha = 0.85f))
                        Text(
                            member?.name ?: "Member",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            member?.memberNumber ?: "-",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    AvatarImage(
                        name = member?.name,
                        photoPath = member?.photoPath,
                        size = 48.dp,
                        bgColor = Color.White.copy(alpha = 0.18f)
                    )
                }

                Spacer(Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "$points",
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.size(6.dp))
                    Text("Poin", color = Color.White.copy(alpha = 0.85f))
                    Spacer(Modifier.size(12.dp))
                    TierBadge(label = tier.label, color = tier.color)
                }

                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = GoldAccent,
                    trackColor = Color.White.copy(alpha = 0.25f)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    if (toNext != null) "$toNext poin lagi menuju ${nextTierLabel(tier)}"
                    else "Anda sudah mencapai tier tertinggi!",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp
                )
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            // ---------- AKSI CEPAT ----------
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAction("Transaksi", Icons.Filled.Add, Modifier.weight(1f), onAddTransaction)
                QuickAction("Riwayat", Icons.AutoMirrored.Filled.ReceiptLong, Modifier.weight(1f), onOpenTransactions)
                QuickAction("Reward", Icons.Filled.CardGiftcard, Modifier.weight(1f), onOpenRewards)
            }

            Spacer(Modifier.height(20.dp))
            // ---------- STATISTIK ----------
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Transaksi", "${transactions.size}", Modifier.weight(1f))
                StatCard("Total Belanja", transactions.sumOf { it.amount }.toRupiah(), Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))
            // ---------- TRANSAKSI TERAKHIR ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Transaksi Terakhir", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (transactions.isNotEmpty()) {
                    Text(
                        "Lihat semua",
                        color = IndigoPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable(onClick = onOpenTransactions)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            if (transactions.isEmpty()) {
                Text(
                    "Belum ada transaksi. Tambahkan transaksi pertama Anda!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                transactions.take(3).forEach { trx ->
                    val cat = TransactionCategory.fromKey(trx.category)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(IndigoPrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(cat.icon, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.size(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(cat.label, fontWeight = FontWeight.SemiBold)
                                Text(trx.date.toIndoDate(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                            Text("+${trx.pointsEarned}", color = IndigoPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun QuickAction(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = IndigoPrimary)
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = IndigoPrimary)
        }
    }
}

private fun nextTierLabel(tier: MemberTier): String = when (tier) {
    MemberTier.BRONZE -> "Silver"
    MemberTier.SILVER -> "Gold"
    MemberTier.GOLD -> "Gold"
}
