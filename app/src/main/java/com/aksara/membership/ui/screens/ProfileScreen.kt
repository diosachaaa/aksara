package com.aksara.membership.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import com.aksara.membership.ui.components.AksaraTopBar
import com.aksara.membership.ui.theme.LavenderCard
import com.aksara.membership.ui.theme.IndigoPrimary
import com.aksara.membership.ui.viewmodel.MembershipViewModel
import com.aksara.membership.util.MemberTier

@Composable
fun ProfileScreen(
    viewModel: MembershipViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val member by viewModel.member.collectAsState()
    var editing by remember { mutableStateOf(false) }

    Scaffold(topBar = { AksaraTopBar("My Profile", onBack = onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(LavenderCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint = IndigoPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(member?.name ?: "-", style = MaterialTheme.typography.titleLarge)
            Text(member?.email ?: "-", style = MaterialTheme.typography.bodyMedium)
            Text(member?.phone ?: "-", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    InfoRow("Nomor Member", member?.memberNumber ?: "-")
                    InfoRow("Tier", MemberTier.of(member?.totalPoints ?: 0).label)
                    InfoRow("Total Poin", "${member?.totalPoints ?: 0}")
                }
            }

            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = { editing = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Edit Profile")
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Logout")
            }
        }
    }

    if (editing) {
        var name by remember { mutableStateOf(member?.name ?: "") }
        var email by remember { mutableStateOf(member?.email ?: "") }
        var phone by remember { mutableStateOf(member?.phone ?: "") }

        AlertDialog(
            onDismissRequest = { editing = false },
            title = { Text("Edit Profil", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") }, singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { input -> phone = input.filter { it.isDigit() } },
                        label = { Text("No HP") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateProfile(name, email, phone)
                    editing = false
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { editing = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}
