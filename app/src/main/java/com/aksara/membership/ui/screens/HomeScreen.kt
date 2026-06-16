package com.aksara.membership.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aksara.membership.ui.components.AksaraTopBar
import com.aksara.membership.ui.theme.LavenderCard
import com.aksara.membership.ui.theme.IndigoPrimary
import com.aksara.membership.ui.viewmodel.MembershipViewModel

@Composable
fun HomeScreen(
    viewModel: MembershipViewModel,
    onOpenCard: () -> Unit,
    onOpenTransactions: () -> Unit,
    onOpenRewards: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val member by viewModel.member.collectAsState()

    Scaffold(topBar = { AksaraTopBar("AKSARA") }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            Text(
                "Hi, ${member?.name ?: "Member"}",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(16.dp))

            // Kartu total poin
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = LavenderCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Points", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${member?.totalPoints ?: 0}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = IndigoPrimary
                        )
                    }
                    Icon(
                        Icons.Filled.AutoStories,
                        contentDescription = null,
                        tint = IndigoPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            MenuItem("My Card", Icons.Filled.CardMembership, onOpenCard)
            Spacer(Modifier.height(12.dp))
            MenuItem("Transactions", Icons.AutoMirrored.Filled.ReceiptLong, onOpenTransactions)
            Spacer(Modifier.height(12.dp))
            MenuItem("Rewards", Icons.Filled.CardGiftcard, onOpenRewards)
            Spacer(Modifier.height(12.dp))
            MenuItem("Profile", Icons.Filled.Person, onOpenProfile)
        }
    }
}

@Composable
private fun MenuItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = IndigoPrimary)
            Spacer(Modifier.size(16.dp))
            Text(title, fontWeight = FontWeight.Medium)
        }
    }
}
