package com.aksara.membership.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aksara.membership.ui.screens.AddTransactionScreen
import com.aksara.membership.ui.screens.HomeScreen
import com.aksara.membership.ui.screens.LoginScreen
import com.aksara.membership.ui.screens.MemberCardScreen
import com.aksara.membership.ui.screens.ProfileScreen
import com.aksara.membership.ui.screens.RegisterScreen
import com.aksara.membership.ui.screens.RewardDetailScreen
import com.aksara.membership.ui.screens.RewardsScreen
import com.aksara.membership.ui.screens.SplashScreen
import com.aksara.membership.ui.screens.TransactionScreen
import com.aksara.membership.ui.viewmodel.MembershipViewModel

@Composable
fun AppNavGraph(viewModel: MembershipViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onStart = {
                    val target = if (viewModel.isLoggedIn) Screen.Home.route else Screen.Login.route
                    navController.navigate(target) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onOpenCard = { navController.navigate(Screen.MemberCard.route) },
                onOpenTransactions = { navController.navigate(Screen.Transactions.route) },
                onOpenRewards = { navController.navigate(Screen.Rewards.route) },
                onOpenProfile = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.MemberCard.route) {
            MemberCardScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }

        composable(Screen.Transactions.route) {
            TransactionScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAddTransaction = { navController.navigate(Screen.AddTransaction.route) }
            )
        }

        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Screen.Rewards.route) {
            RewardsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSelectReward = { rewardId ->
                    navController.navigate(Screen.RewardDetail.createRoute(rewardId))
                }
            )
        }

        composable(
            route = Screen.RewardDetail.route,
            arguments = listOf(navArgument("rewardId") { type = NavType.LongType })
        ) { backStackEntry ->
            val rewardId = backStackEntry.arguments?.getLong("rewardId") ?: 0L
            RewardDetailScreen(
                viewModel = viewModel,
                rewardId = rewardId,
                onBack = { navController.popBackStack() },
                onDone = {
                    navController.popBackStack(Screen.Rewards.route, inclusive = false)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
