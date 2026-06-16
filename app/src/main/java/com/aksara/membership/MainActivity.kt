package com.aksara.membership

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aksara.membership.ui.navigation.AppNavGraph
import com.aksara.membership.ui.theme.AksaraTheme
import com.aksara.membership.ui.viewmodel.MembershipViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AksaraTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val app = application as AksaraApp
                    val viewModel: MembershipViewModel = viewModel(
                        factory = MembershipViewModel.Factory(app.repository)
                    )
                    AppNavGraph(viewModel = viewModel)
                }
            }
        }
    }
}
