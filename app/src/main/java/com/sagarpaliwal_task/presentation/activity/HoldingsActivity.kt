package com.sagarpaliwal_task.presentation.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sagarpaliwal_task.R
import com.sagarpaliwal_task.core.model.UserHolding
import com.sagarpaliwal_task.databinding.ActivityHoldingsBinding
import com.sagarpaliwal_task.presentation.adapter.HoldingsAdapter
import com.sagarpaliwal_task.core.model.PortfolioSummary
import com.sagarpaliwal_task.presentation.viewmodel.HoldingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

enum class SortType {
    NAME, LTP, QUANTITY, PNL
}

class HoldingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHoldingsBinding
    private val viewModel: HoldingsViewModel by viewModel()
    private lateinit var holdingsAdapter: HoldingsAdapter
    private var isSummaryExpanded = false
    private var allHoldings: List<UserHolding> = emptyList()
    private var filteredHoldings: List<UserHolding> = emptyList()
    private var currentSortType = SortType.NAME
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        

        
        binding = ActivityHoldingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        
        // Load holdings data
        viewModel.loadHoldings()
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
            Toast.makeText(this, "Profile settings coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        // Sort icon click
        binding.sortIcon.setOnClickListener {
            showSortDialog()
        }
        
        // Search icon click
        binding.searchIcon.setOnClickListener {
            showSearchDialog()
        }
    }
    
    private fun setupObservers() {
        // Observe holdings data
        viewModel.holdingsData.observe(this, Observer { holdings ->
            holdings?.let {
                allHoldings = it.data.userHolding
                filteredHoldings = allHoldings
                applySortAndFilter()
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
    
    private fun calculatePortfolioSummary(holdings: List<UserHolding>): PortfolioSummary {
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
    
    private fun showSortDialog() {
        val sortOptions = arrayOf("Name (A-Z)", "LTP (High to Low)", "Quantity (High to Low)", "P&L (High to Low)")
        val currentSelection = when (currentSortType) {
            SortType.NAME -> 0
            SortType.LTP -> 1
            SortType.QUANTITY -> 2
            SortType.PNL -> 3
        }
        
        AlertDialog.Builder(this)
            .setTitle("Sort Holdings")
            .setSingleChoiceItems(sortOptions, currentSelection) { dialog, which ->
                currentSortType = when (which) {
                    0 -> SortType.NAME
                    1 -> SortType.LTP
                    2 -> SortType.QUANTITY
                    3 -> SortType.PNL
                    else -> SortType.NAME
                }
                applySortAndFilter()
                dialog.dismiss()
                Toast.makeText(this, "Sorted by ${sortOptions[which]}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showSearchDialog() {
        // Create main container with Apple-style design
        val mainLayout = android.widget.LinearLayout(this)
        mainLayout.orientation = android.widget.LinearLayout.VERTICAL
        mainLayout.setPadding(0, 0, 0, 0)
        mainLayout.setBackgroundColor(getColor(R.color.white))
        
        // Create rounded corner background
        val backgroundDrawable = android.graphics.drawable.GradientDrawable()
        backgroundDrawable.setColor(getColor(R.color.white))
        backgroundDrawable.cornerRadius = 24f
        backgroundDrawable.setStroke(1, getColor(R.color.border_color))
        mainLayout.background = backgroundDrawable
        
        // Title section
        val titleLayout = android.widget.LinearLayout(this)
        titleLayout.orientation = android.widget.LinearLayout.VERTICAL
        titleLayout.setPadding(32, 32, 32, 16)
        
        val titleText = android.widget.TextView(this)
        titleText.text = "Search Holdings"
        titleText.textSize = 22f
        titleText.setTextColor(getColor(R.color.text_primary))
        titleText.typeface = android.graphics.Typeface.DEFAULT_BOLD
        titleText.setPadding(0, 0, 0, 8)
        
        val subtitleText = android.widget.TextView(this)
        subtitleText.text = "Find stocks by symbol"
        subtitleText.textSize = 16f
        subtitleText.setTextColor(getColor(R.color.text_muted))
        subtitleText.setPadding(0, 0, 0, 24)
        
        titleLayout.addView(titleText)
        titleLayout.addView(subtitleText)
        
        // Search input container
        val inputContainer = android.widget.LinearLayout(this)
        inputContainer.orientation = android.widget.LinearLayout.HORIZONTAL
        inputContainer.setPadding(24, 16, 24, 16)
        inputContainer.gravity = android.view.Gravity.CENTER_VERTICAL
        
        // Create rounded search input
        val searchInput = android.widget.EditText(this)
        searchInput.hint = "Enter symbol (e.g., RELIANCE, TCS)"
        searchInput.setText("")
        searchInput.textSize = 16f
        searchInput.setTextColor(getColor(R.color.text_primary))
        searchInput.setHintTextColor(getColor(R.color.text_muted))
        searchInput.setPadding(20, 20, 20, 20)
        searchInput.background = createRoundedInputBackground()
        searchInput.layoutParams = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        )
        
        inputContainer.addView(searchInput)
        
        // Button container
        val buttonContainer = android.widget.LinearLayout(this)
        buttonContainer.orientation = android.widget.LinearLayout.HORIZONTAL
        buttonContainer.setPadding(24, 8, 24, 32)
        buttonContainer.gravity = android.view.Gravity.END
        
        // Search button
        val searchButton = createAppleStyleButton("Search", true)
        val clearButton = createAppleStyleButton("Clear", false)
        
        val buttonLayoutParams = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        )
        buttonLayoutParams.setMargins(0, 0, 12, 0)
        
        searchButton.layoutParams = buttonLayoutParams
        clearButton.layoutParams = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        )
        
        buttonContainer.addView(searchButton)
        buttonContainer.addView(clearButton)
        
        // Add all sections to main layout
        mainLayout.addView(titleLayout)
        mainLayout.addView(inputContainer)
        mainLayout.addView(buttonContainer)
        
        // Create and show dialog
        val dialog = AlertDialog.Builder(this)
            .setView(mainLayout)
            .setCancelable(true)
            .create()
        
        // Set click listeners
        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()
            if (query.isNotEmpty()) {
                searchHoldings(query)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter a symbol to search", Toast.LENGTH_SHORT).show()
            }
        }
        
        clearButton.setOnClickListener {
            filteredHoldings = allHoldings
            applySortAndFilter()
            dialog.dismiss()
            Toast.makeText(this, "Search cleared", Toast.LENGTH_SHORT).show()
        }
        
        dialog.show()
    }
    
    private fun createRoundedInputBackground(): android.graphics.drawable.Drawable {
        val drawable = android.graphics.drawable.GradientDrawable()
        drawable.setColor(getColor(R.color.background_light))
        drawable.cornerRadius = 16f
        drawable.setStroke(2, getColor(R.color.border_color))
        return drawable
    }
    
    private fun createAppleStyleButton(text: String, isPrimary: Boolean): android.widget.Button {
        val button = android.widget.Button(this)
        button.text = text
        button.textSize = 16f
        button.setPadding(24, 16, 24, 16)
        button.minWidth = 100
        
        val background = android.graphics.drawable.GradientDrawable()
        if (isPrimary) {
            background.setColor(getColor(R.color.portfolio_blue))
            button.setTextColor(getColor(R.color.white))
        } else {
            background.setColor(getColor(R.color.background_light))
            button.setTextColor(getColor(R.color.text_primary))
            background.setStroke(2, getColor(R.color.border_color))
        }
        background.cornerRadius = 12f
        button.background = background
        
        return button
    }
    
    private fun searchHoldings(query: String) {
        filteredHoldings = allHoldings.filter { holding ->
            holding.symbol.contains(query, ignoreCase = true)
        }
        applySortAndFilter()
        
        if (filteredHoldings.isEmpty()) {
            Toast.makeText(this, "No holdings found for '$query'", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Found ${filteredHoldings.size} holdings", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun applySortAndFilter() {
        val sortedHoldings = when (currentSortType) {
            SortType.NAME -> filteredHoldings.sortedBy { it.symbol }
            SortType.LTP -> filteredHoldings.sortedByDescending { it.ltp }
            SortType.QUANTITY -> filteredHoldings.sortedByDescending { it.quantity }
            SortType.PNL -> filteredHoldings.sortedByDescending { (it.ltp - it.avgPrice) * it.quantity }
        }
        
        holdingsAdapter.submitList(sortedHoldings)
    }
}
