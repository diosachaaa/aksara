package com.aksara.membership.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.aksara.membership.ui.navigation.Screen
import com.aksara.membership.ui.theme.IndigoPrimary

private data class TabItem(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    TabItem(Screen.Home.route, "Home", Icons.Filled.Home),
    TabItem(Screen.MemberCard.route, "Kartu", Icons.Filled.CardMembership),
    TabItem(Screen.Rewards.route, "Reward", Icons.Filled.CardGiftcard),
    TabItem(Screen.Profile.route, "Profil", Icons.Filled.Person)
)

/** Daftar rute yang menampilkan bottom bar. */
val bottomBarRoutes: Set<String> = tabs.map { it.route }.toSet()

@Composable
fun AksaraBottomBar(currentRoute: String?, onSelect: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = { if (currentRoute != tab.route) onSelect(tab.route) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = IndigoPrimary,
                    selectedTextColor = IndigoPrimary,
                    indicatorColor = IndigoPrimary.copy(alpha = 0.12f)
                )
            )
        }
    }
}
