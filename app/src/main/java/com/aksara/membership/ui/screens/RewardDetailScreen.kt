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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.AutoStories
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aksara.membership.ui.components.AksaraTopBar
import com.aksara.membership.ui.components.PrimaryButton
import com.aksara.membership.ui.theme.LavenderCard
import com.aksara.membership.ui.theme.IndigoPrimary
import com.aksara.membership.ui.viewmodel.MembershipViewModel

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
    var showSuccess by remember { mutableStateOf(false) }
    var remainingPoints by remember { mutableStateOf(0) }

    val currentPoints = member?.totalPoints ?: 0
    val enoughPoints = reward != null && currentPoints >= reward.pointCost

    Scaffold(topBar = { AksaraTopBar("Reward Detail", onBack = onBack) }) { padding ->
        if (reward == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Reward tidak ditemukan.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(LavenderCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.AutoStories,
                    contentDescription = null,
                    tint = IndigoPrimary,
                    modifier = Modifier.size(56.dp)
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(reward.name, style = MaterialTheme.typography.headlineMedium)
            Text(
                "${reward.pointCost} Poin",
                style = MaterialTheme.typography.titleLarge,
                color = IndigoPrimary
            )
            Text(
                reward.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Total Poin Anda: $currentPoints",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(32.dp))
            PrimaryButton(text = "Redeem", enabled = enoughPoints) { showConfirm = true }

            if (!enoughPoints) {
                Text(
                    "Poin Anda belum mencukupi untuk reward ini.",
                    color = androidx.compose.ui.graphics.Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }

    // Langkah 9: Konfirmasi penukaran
    if (showConfirm && reward != null) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Konfirmasi", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Tukar reward ini?")
                    Spacer(Modifier.height(8.dp))
                    Text("${reward.name} (${reward.pointCost} Poin)", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text("Total Points: $currentPoints")
                    Text("Setelah ditukar: ${currentPoints - reward.pointCost}")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.redeem(reward) { success ->
                        showConfirm = false
                        if (success) {
                            remainingPoints = currentPoints - reward.pointCost
                            showSuccess = true
                        }
                    }
                }) { Text("Konfirmasi") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Batal") }
            }
        )
    }

    // Langkah 10: Redeem Success
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = IndigoPrimary) },
            title = { Text("Berhasil!", fontWeight = FontWeight.Bold) },
            text = { Text("Reward berhasil ditukar.\nSisa poin Anda: $remainingPoints") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccess = false
                    onDone()
                }) { Text("OK") }
            }
        )
    }
}
