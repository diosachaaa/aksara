package com.aksara.membership.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksara.membership.ui.components.AksaraTopBar
import com.aksara.membership.ui.components.PrimaryButton
import com.aksara.membership.ui.theme.IndigoPrimary
import com.aksara.membership.ui.viewmodel.MembershipViewModel
import com.aksara.membership.util.rememberQrBitmap

@Composable
fun RewardDetailScreen(
    viewModel: MembershipViewModel,
    rewardId: Long,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    val rewards by viewModel.rewards.collectAsState()
    val member by viewModel.member.collectAsState()
    val reward = rewards.firstOrNull { it.id == rewardId }

    var showConfirm by remember { mutableStateOf(false) }
    var voucherCode by remember { mutableStateOf<String?>(null) }

    val currentPoints = member?.totalPoints ?: 0
    val enough = reward != null && currentPoints >= reward.pointCost

    Scaffold(topBar = { AksaraTopBar("Detail Reward", onBack = onBack) }) { padding ->
        if (reward == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Reward tidak ditemukan.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(120.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.AutoStories, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(56.dp))
            }
            Spacer(Modifier.height(20.dp))
            Text(reward.name, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Text("${reward.pointCost} Poin", style = MaterialTheme.typography.titleLarge, color = IndigoPrimary)
            Text(
                reward.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text("Total Poin Anda: $currentPoints", fontWeight = FontWeight.SemiBold)

            Spacer(Modifier.height(32.dp))
            PrimaryButton(text = "Tukar Sekarang", enabled = enough) { showConfirm = true }
            if (!enough) {
                Text(
                    "Poin Anda belum mencukupi untuk reward ini.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // Konfirmasi
    if (showConfirm && reward != null) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Konfirmasi Penukaran", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Tukar reward berikut?")
                    Spacer(Modifier.height(8.dp))
                    Text("${reward.name} (${reward.pointCost} Poin)", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text("Poin sekarang: $currentPoints")
                    Text("Sisa setelah ditukar: ${currentPoints - reward.pointCost}")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.redeem(reward) { code ->
                        showConfirm = false
                        if (code != null) voucherCode = code
                    }
                }) { Text("Konfirmasi") }
            },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("Batal") } }
        )
    }

    // Sukses + kode voucher & QR
    voucherCode?.let { code ->
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = IndigoPrimary) },
            title = { Text("Penukaran Berhasil!", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Tunjukkan kode voucher ini ke kasir:", textAlign = TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                    val qr = rememberQrBitmap(code, 256)
                    if (qr != null) {
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White).padding(10.dp)
                        ) {
                            Image(bitmap = qr, contentDescription = "QR Voucher", modifier = Modifier.size(140.dp))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(IndigoPrimary.copy(alpha = 0.12f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(code, fontWeight = FontWeight.Bold, color = IndigoPrimary, letterSpacing = 1.sp)
                    }
                }
            },
            confirmButton = { TextButton(onClick = { voucherCode = null; onDone() }) { Text("Selesai") } }
        )
    }
}
