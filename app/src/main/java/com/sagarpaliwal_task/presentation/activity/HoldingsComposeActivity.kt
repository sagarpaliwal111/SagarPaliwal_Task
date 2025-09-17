package com.sagarpaliwal_task.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.sagarpaliwal_task.presentation.compose.HoldingsScreen
import com.sagarpaliwal_task.presentation.viewmodel.HoldingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HoldingsComposeActivity : ComponentActivity() {

    private val viewModel: HoldingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load holdings data
        viewModel.loadHoldings()

        setContent {
            MaterialTheme {
                HoldingsScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() }
                )
            }
        }
    }
}
