package com.aksara.membership.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aksara.membership.ui.components.AksaraTopBar
import com.aksara.membership.ui.theme.LavenderCard
import com.aksara.membership.ui.theme.IndigoPrimary
import com.aksara.membership.ui.viewmodel.MembershipViewModel

@Composable
fun RewardsScreen(
    viewModel: MembershipViewModel,
    onBack: () -> Unit,
    onSelectReward: (Long) -> Unit
) {
    val rewards by viewModel.rewards.collectAsState()
    val member by viewModel.member.collectAsState()

    Scaffold(topBar = { AksaraTopBar("Rewards", onBack = onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Poin Anda: ${member?.totalPoints ?: 0}",
                style = MaterialTheme.typography.titleMedium,
                color = IndigoPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(rewards) { reward ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectReward(reward.id) },
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.AutoStories, contentDescription = null, tint = IndigoPrimary)
                            }
                            Spacer(Modifier.size(16.dp))
                            Column {
                                Text(
                                    "${reward.pointCost} Poin",
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoPrimary
                                )
                                Text(reward.name, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
