package com.sagarpaliwal_task.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sagarpaliwal_task.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    
    private val viewModel: MainViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        setupObservers()
        viewModel.loadHoldings()
    }
    
    private fun setupObservers() {
        viewModel.holdingsResult.observe(this) { result ->
            result?.let {
                if (it.isSuccess) {
                    val holdings = it.getOrNull()
                    Toast.makeText(
                        this,
                        "API called successfully! Found ${holdings?.data?.userHolding?.size ?: 0} holdings",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val error = it.exceptionOrNull()
                    Toast.makeText(
                        this,
                        "API call failed: ${error?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}