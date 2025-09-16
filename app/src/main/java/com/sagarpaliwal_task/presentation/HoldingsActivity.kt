package com.sagarpaliwal_task.presentation

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sagarpaliwal_task.R
import com.sagarpaliwal_task.databinding.ActivityHoldingsBinding
import com.sagarpaliwal_task.presentation.adapter.HoldingsAdapter
import com.sagarpaliwal_task.presentation.model.PortfolioSummary
import org.koin.androidx.viewmodel.ext.android.viewModel

class HoldingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHoldingsBinding
    private val viewModel: HoldingsViewModel by viewModel()
    private lateinit var holdingsAdapter: HoldingsAdapter
    private var isSummaryExpanded = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle system UI for edge-to-edge display
        setupSystemUI()
        
        binding = ActivityHoldingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        
        // Load holdings data
        viewModel.loadHoldings()
    }
    
    @SuppressLint("NewApi")
    private fun setupSystemUI() {
        // Set status bar color to match app bar
        window.statusBarColor = getColor(R.color.portfolio_blue)
        
        // Make status bar content light (for white status bar icons)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false
    }
    
    private fun setupRecyclerView() {
        holdingsAdapter = HoldingsAdapter()
        binding.recyclerViewHoldings.apply {
            layoutManager = LinearLayoutManager(this@HoldingsActivity)
            adapter = holdingsAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.summaryCollapsed.setOnClickListener {
            toggleSummaryExpansion()
        }
        
        // Profile icon click
        binding.profileIcon.setOnClickListener {
            android.widget.Toast.makeText(this, "Profile settings coming soon!", android.widget.Toast.LENGTH_SHORT).show()
        }
        
        // Sort icon click
        binding.sortIcon.setOnClickListener {
            android.widget.Toast.makeText(this, "Sort by: Name, Value, P&L", android.widget.Toast.LENGTH_SHORT).show()
        }
        
        // Search icon click
        binding.searchIcon.setOnClickListener {
            android.widget.Toast.makeText(this, "Search holdings by symbol or name", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupObservers() {
        // Observe holdings data
        viewModel.holdingsData.observe(this, Observer { holdings ->
            holdings?.let {
                holdingsAdapter.submitList(it.data.userHolding)
                calculateAndUpdateSummary(it.data.userHolding)
            }
        })
        
        // Observe error messages
        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                // Handle error - could show snackbar or toast
            }
        })
        
        // Observe loading state
        viewModel.loader.observe(this, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
    }
    
    private fun calculateAndUpdateSummary(holdings: List<com.sagarpaliwal_task.core.model.UserHolding>) {
        val summary = calculatePortfolioSummary(holdings)
        updateSummaryUI(summary)
    }
    
    private fun calculatePortfolioSummary(holdings: List<com.sagarpaliwal_task.core.model.UserHolding>): PortfolioSummary {
        var currentValue = 0.0
        var totalInvestment = 0.0
        var todaysPnL = 0.0
        
        holdings.forEach { holding ->
            // Current value = LTP * quantity
            currentValue += holding.ltp * holding.quantity
            
            // Total investment = avgPrice * quantity
            totalInvestment += holding.avgPrice * holding.quantity
            
            // Today's P&L = (close - LTP) * quantity
            todaysPnL += (holding.close - holding.ltp) * holding.quantity
        }
        
        val totalPnL = currentValue - totalInvestment
        val totalPnLPercentage = if (totalInvestment != 0.0) {
            (totalPnL / totalInvestment) * 100
        } else 0.0
        
        return PortfolioSummary(
            currentValue = currentValue,
            totalInvestment = totalInvestment,
            totalPnL = totalPnL,
            totalPnLPercentage = totalPnLPercentage,
            todaysPnL = todaysPnL
        )
    }
    
    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun updateSummaryUI(summary: PortfolioSummary) {
        // Update collapsed state
        val pnlText = if (summary.totalPnL >= 0) {
            "+₹${String.format("%.2f", summary.totalPnL)} (${String.format("%.2f", summary.totalPnLPercentage)}%)"
        } else {
            "₹${String.format("%.2f", summary.totalPnL)} (${String.format("%.2f", summary.totalPnLPercentage)}%)"
        }
        
        binding.totalPnLCollapsed.text = pnlText
        binding.totalPnLCollapsed.setTextColor(
            if (summary.totalPnL >= 0) getColor(R.color.profit_green) else getColor(R.color.loss_red)
        )
        
        // Update expanded state
        binding.currentValue.text = "₹${String.format("%.2f", summary.currentValue)}"
        binding.totalInvestment.text = "₹${String.format("%.2f", summary.totalInvestment)}"
        
        val todaysPnLText = if (summary.todaysPnL >= 0) {
            "+₹${String.format("%.2f", summary.todaysPnL)}"
        } else {
            "₹${String.format("%.2f", summary.todaysPnL)}"
        }
        
        binding.todaysPnL.text = todaysPnLText
        binding.todaysPnL.setTextColor(
            if (summary.todaysPnL >= 0) getColor(R.color.profit_green) else getColor(R.color.loss_red)
        )

    }
    
    private fun toggleSummaryExpansion() {
        isSummaryExpanded = !isSummaryExpanded
        
        if (isSummaryExpanded) {
            expandSummary()
        } else {
            collapseSummary()
        }
    }
    
    private fun expandSummary() {
        binding.summaryExpanded.visibility = View.VISIBLE
        
        // Animate expand icon rotation
        val rotationAnimator = ValueAnimator.ofFloat(0f, 180f)
        rotationAnimator.duration = 300
        rotationAnimator.interpolator = AccelerateDecelerateInterpolator()
        rotationAnimator.addUpdateListener { animator ->
            binding.expandIcon.rotation = animator.animatedValue as Float
        }
        rotationAnimator.start()
        
        // Animate height expansion
        binding.summaryExpanded.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        
        val targetHeight = binding.summaryExpanded.measuredHeight
        val heightAnimator = ValueAnimator.ofInt(0, targetHeight)
        heightAnimator.duration = 300
        heightAnimator.interpolator = AccelerateDecelerateInterpolator()
        heightAnimator.addUpdateListener { animator ->
            val layoutParams = binding.summaryExpanded.layoutParams
            layoutParams.height = animator.animatedValue as Int
            binding.summaryExpanded.layoutParams = layoutParams
        }
        heightAnimator.start()
    }
    
    private fun collapseSummary() {
        // Animate expand icon rotation
        val rotationAnimator = ValueAnimator.ofFloat(180f, 0f)
        rotationAnimator.duration = 300
        rotationAnimator.interpolator = AccelerateDecelerateInterpolator()
        rotationAnimator.addUpdateListener { animator ->
            binding.expandIcon.rotation = animator.animatedValue as Float
        }
        rotationAnimator.start()
        
        // Animate height collapse
        val currentHeight = binding.summaryExpanded.height
        val heightAnimator = ValueAnimator.ofInt(currentHeight, 0)
        heightAnimator.duration = 300
        heightAnimator.interpolator = AccelerateDecelerateInterpolator()
        heightAnimator.addUpdateListener { animator ->
            val layoutParams = binding.summaryExpanded.layoutParams
            layoutParams.height = animator.animatedValue as Int
            binding.summaryExpanded.layoutParams = layoutParams
        }
        heightAnimator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                binding.summaryExpanded.visibility = View.GONE
            }
        })
        heightAnimator.start()
    }
}
