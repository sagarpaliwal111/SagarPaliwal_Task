package com.sagarpaliwal_task.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.sagarpaliwal_task.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    
    private val viewModel: MainViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Observer
        setupObservers()

        // Load holdings data
        viewModel.loadHoldings()
    }
    
    private fun setupObservers() {
        // Observe holdings data
        viewModel.holdingsData.observe(this, Observer { holdings ->
            holdings?.let {
                Toast.makeText(
                    this,
                    "API called successfully! Found ${it.data.userHolding.size} holdings",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        
        // Observe error messages
        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(
                    this,
                    "API call failed: $it",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        
        // Observe loading state
        viewModel.loader.observe(this, Observer { isLoading ->
            // You can show/hide loading indicator here
            if (isLoading) {
                Toast.makeText(this, "Loading holdings...", Toast.LENGTH_SHORT).show()
            }
        })
    }
}