package com.aksara.membership.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aksara.membership.ui.components.AksaraTopBar
import com.aksara.membership.ui.components.AvatarImage
import com.aksara.membership.ui.components.TierBadge
import com.aksara.membership.ui.components.brandBrush
import com.aksara.membership.ui.theme.IndigoPrimary
import com.aksara.membership.ui.viewmodel.MembershipViewModel
import com.aksara.membership.util.ImageStorage
import com.aksara.membership.util.MemberTier

@Composable
fun ProfileScreen(
    viewModel: MembershipViewModel,
    onOpenHistory: () -> Unit,
    onLogout: () -> Unit
) {
    val member by viewModel.member.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    var editing by remember { mutableStateOf(false) }
    val tier = MemberTier.of(member?.totalPoints ?: 0)

    Scaffold(topBar = { AksaraTopBar("Profil") }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().background(brandBrush()).padding(vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AvatarImage(
                    name = member?.name,
                    photoPath = member?.photoPath,
                    size = 88.dp,
                    bgColor = Color.White.copy(alpha = 0.18f)
                )
                Spacer(Modifier.height(12.dp))
                Text(member?.name ?: "-", color = Color.White, style = MaterialTheme.typography.titleLarge)
                Text(member?.email ?: "-", color = Color.White.copy(alpha = 0.85f))
                Spacer(Modifier.height(8.dp))
                TierBadge(label = tier.label, color = tier.color)
            }

            Column(Modifier.padding(20.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        InfoRow("Nomor Member", member?.memberNumber ?: "-")
                        InfoRow("No HP", member?.phone ?: "-")
                        InfoRow("Tier", tier.label)
                        InfoRow("Total Poin", "${member?.totalPoints ?: 0}")
                    }
                }

                Spacer(Modifier.height(16.dp))
                MenuRow("Riwayat Penukaran", Icons.Filled.History, onClick = onOpenHistory)
                MenuRow("Edit Profil", Icons.Filled.Edit, onClick = { editing = true })

                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.DarkMode, contentDescription = null, tint = IndigoPrimary)
                        Spacer(Modifier.size(16.dp))
                        Text("Mode Gelap", Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        Switch(checked = darkMode, onCheckedChange = { viewModel.toggleDarkMode() })
                    }
                }

                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Logout")
                }
            }
        }
    }

    if (editing) {
        val context = LocalContext.current
        val memberId = member?.id ?: 0L
        var name by remember { mutableStateOf(member?.name ?: "") }
        var email by remember { mutableStateOf(member?.email ?: "") }
        var phone by remember { mutableStateOf(member?.phone ?: "") }
        var photoPath by remember { mutableStateOf(member?.photoPath) }

        val picker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                photoPath = ImageStorage.copyToInternal(context, uri, memberId)
            }
        }

        AlertDialog(
            onDismissRequest = { editing = false },
            title = { Text("Edit Profil", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AvatarImage(
                                name = name,
                                photoPath = photoPath,
                                size = 76.dp,
                                bgColor = IndigoPrimary.copy(alpha = 0.15f),
                                textColor = IndigoPrimary
                            )
                            Spacer(Modifier.height(6.dp))
                            TextButton(onClick = { picker.launch("image/*") }) {
                                Icon(Icons.Filled.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.size(6.dp))
                                Text("Ubah Foto")
                            }
                        }
                    }
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
                    viewModel.updateProfile(name, email, phone, photoPath)
                    editing = false
                }) { Text("Simpan") }
            },
            dismissButton = { TextButton(onClick = { editing = false }) { Text("Batal") } }
        )
    }
}

@Composable
private fun MenuRow(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = IndigoPrimary)
            Spacer(Modifier.size(16.dp))
            Text(title, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}
