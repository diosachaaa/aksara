package com.aksara.membership.ui.navigation

/** Definisi semua rute navigasi aplikasi. */
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Register : Screen("register")
    data object Login : Screen("login")

    // Tab utama (punya bottom bar)
    data object Home : Screen("home")
    data object MemberCard : Screen("member_card")
    data object Rewards : Screen("rewards")
    data object Profile : Screen("profile")

    // Layar detail (tanpa bottom bar)
    data object Transactions : Screen("transactions")
    data object Catalog : Screen("catalog")
    data object Cart : Screen("cart")
    data object RedemptionHistory : Screen("redemption_history")

    data object RewardDetail : Screen("reward_detail/{rewardId}") {
        fun createRoute(rewardId: Long) = "reward_detail/$rewardId"
    }
}
