package com.aksara.membership.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aksara.membership.ui.components.AksaraBottomBar
import com.aksara.membership.ui.components.bottomBarRoutes
import com.aksara.membership.ui.screens.CartScreen
import com.aksara.membership.ui.screens.CatalogScreen
import com.aksara.membership.ui.screens.HomeScreen
import com.aksara.membership.ui.screens.LoginScreen
import com.aksara.membership.ui.screens.MemberCardScreen
import com.aksara.membership.ui.screens.ProfileScreen
import com.aksara.membership.ui.screens.RedemptionHistoryScreen
import com.aksara.membership.ui.screens.RegisterScreen
import com.aksara.membership.ui.screens.RewardDetailScreen
import com.aksara.membership.ui.screens.RewardsScreen
import com.aksara.membership.ui.screens.SplashScreen
import com.aksara.membership.ui.screens.TransactionScreen
import com.aksara.membership.ui.viewmodel.MembershipViewModel
import com.aksara.membership.ui.viewmodel.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(viewModel: MembershipViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }
    val event by viewModel.event.collectAsState()

    // Tampilkan snackbar untuk setiap event sukses/gagal
    LaunchedEffect(event) {
        event?.let {
            val msg = when (it) {
                is UiEvent.Error -> it.message
                is UiEvent.Success -> it.message
            }
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.consumeEvent()
        }
    }

    fun selectTab(route: String) {
        navController.navigate(route) {
            popUpTo(Screen.Home.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                AksaraBottomBar(currentRoute = currentRoute, onSelect = ::selectTab)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onStart = {
                    val target = if (viewModel.isLoggedIn) Screen.Home.route else Screen.Login.route
                    navController.navigate(target) { popUpTo(Screen.Splash.route) { inclusive = true } }
                })
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                    },
                    onGoToRegister = { navController.navigate(Screen.Register.route) }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = viewModel,
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onOpenCard = { selectTab(Screen.MemberCard.route) },
                    onOpenTransactions = { navController.navigate(Screen.Transactions.route) },
                    onOpenRewards = { selectTab(Screen.Rewards.route) },
                    onAddTransaction = { navController.navigate(Screen.Catalog.route) }
                )
            }

            composable(Screen.MemberCard.route) {
                MemberCardScreen(viewModel = viewModel)
            }

            composable(Screen.Rewards.route) {
                RewardsScreen(
                    viewModel = viewModel,
                    onSelectReward = { rewardId -> navController.navigate(Screen.RewardDetail.createRoute(rewardId)) },
                    onOpenHistory = { navController.navigate(Screen.RedemptionHistory.route) }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onOpenHistory = { navController.navigate(Screen.RedemptionHistory.route) },
                    onLogout = {
                        viewModel.logout()
                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            composable(Screen.Transactions.route) {
                TransactionScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onAddTransaction = { navController.navigate(Screen.Catalog.route) }
                )
            }

            composable(Screen.Catalog.route) {
                CatalogScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenCart = { navController.navigate(Screen.Cart.route) }
                )
            }

            composable(Screen.Cart.route) {
                CartScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onPaid = { navController.popBackStack(Screen.Home.route, inclusive = false) }
                )
            }

            composable(Screen.RedemptionHistory.route) {
                RedemptionHistoryScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }

            composable(
                route = Screen.RewardDetail.route,
                arguments = listOf(navArgument("rewardId") { type = NavType.LongType })
            ) { entry ->
                val rewardId = entry.arguments?.getLong("rewardId") ?: 0L
                RewardDetailScreen(
                    viewModel = viewModel,
                    rewardId = rewardId,
                    onBack = { navController.popBackStack() },
                    onDone = { navController.popBackStack(Screen.Rewards.route, inclusive = false) }
                )
            }
        }
    }
}
