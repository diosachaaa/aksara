package com.aksara.membership.ui.navigation

/** Definisi semua rute (tujuan) navigasi aplikasi. */
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Register : Screen("register")
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object MemberCard : Screen("member_card")
    data object Transactions : Screen("transactions")
    data object AddTransaction : Screen("add_transaction")
    data object Rewards : Screen("rewards")
    data object Profile : Screen("profile")

    // Rute dengan argumen reward id
    data object RewardDetail : Screen("reward_detail/{rewardId}") {
        fun createRoute(rewardId: Long) = "reward_detail/$rewardId"
    }
}
