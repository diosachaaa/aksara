package com.aksara.membership.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.aksara.membership.ui.theme.IndigoPrimary
import com.aksara.membership.ui.viewmodel.MembershipViewModel
import com.aksara.membership.util.MemberTier
import com.aksara.membership.util.rememberQrBitmap

@Composable
fun MemberCardScreen(viewModel: MembershipViewModel, onBack: () -> Unit) {
    val member by viewModel.member.collectAsState()

    Scaffold(topBar = { AksaraTopBar("My Membership Card", onBack = onBack) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("AKSARA MEMBER", color = Color.White, fontWeight = FontWeight.Bold)

                    Text(
                        member?.name?.uppercase() ?: "-",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        "ID : ${member?.memberNumber ?: "-"}",
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        "Tier : ${MemberTier.of(member?.totalPoints ?: 0).label}",
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    // QR Code dari nomor member
                    val qr = rememberQrBitmap(member?.memberNumber ?: "AKSARA")
                    Box(
                        modifier = Modifier
                            .padding(vertical = 20.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(12.dp)
                    ) {
                        if (qr != null) {
                            Image(
                                bitmap = qr,
                                contentDescription = "QR Member",
                                modifier = Modifier.size(180.dp)
                            )
                        }
                    }

                    Text("POINTS", color = Color.White.copy(alpha = 0.9f))
                    Text(
                        "${member?.totalPoints ?: 0}",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
    }
}
