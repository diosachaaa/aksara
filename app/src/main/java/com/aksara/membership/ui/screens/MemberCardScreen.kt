package com.aksara.membership.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import com.aksara.membership.ui.components.AksaraTopBar
import com.aksara.membership.ui.components.TierBadge
import com.aksara.membership.ui.components.brandBrush
import com.aksara.membership.ui.viewmodel.MembershipViewModel
import com.aksara.membership.util.MemberTier
import com.aksara.membership.util.rememberQrBitmap

@Composable
fun MemberCardScreen(viewModel: MembershipViewModel) {
    val member by viewModel.member.collectAsState()
    val tier = MemberTier.of(member?.totalPoints ?: 0)

    Scaffold(topBar = { AksaraTopBar("Kartu Member") }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(brandBrush())
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "AKSARA MEMBER",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    TierBadge(label = tier.label, color = tier.color)
                }

                Spacer(Modifier.height(20.dp))
                Text(
                    member?.name?.uppercase() ?: "-",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
                Text("ID : ${member?.memberNumber ?: "-"}", color = Color.White.copy(alpha = 0.9f))

                val qr = rememberQrBitmap(member?.memberNumber ?: "AKSARA")
                Box(
                    modifier = Modifier
                        .padding(vertical = 22.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(14.dp)
                ) {
                    if (qr != null) {
                        Image(bitmap = qr, contentDescription = "QR Member", modifier = Modifier.size(190.dp))
                    }
                }

                Text("TOTAL POIN", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                Text(
                    "${member?.totalPoints ?: 0}",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Tunjukkan QR ini ke kasir untuk transaksi",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
