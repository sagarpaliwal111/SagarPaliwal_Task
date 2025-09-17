package com.sagarpaliwal_task.presentation.compose

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.livedata.observeAsState
import com.sagarpaliwal_task.R
import com.sagarpaliwal_task.core.model.UserHolding
import com.sagarpaliwal_task.presentation.viewmodel.HoldingsViewModel
import com.sagarpaliwal_task.core.model.PortfolioSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoldingsScreen(
    viewModel: HoldingsViewModel,
    onBackClick: () -> Unit
) {
    val holdingsData by viewModel.holdingsData.observeAsState()
    val isLoading by viewModel.loader.observeAsState()

    var isSummaryExpanded by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var currentSortType by remember { mutableStateOf(SortType.NAME) }
    
    val holdings = holdingsData?.data?.userHolding ?: emptyList()
    val filteredHoldings = remember(holdings, searchQuery, currentSortType) {
        holdings
            .filter { it.symbol.contains(searchQuery, ignoreCase = true) }
            .let { filtered ->
                when (currentSortType) {
                    SortType.NAME -> filtered.sortedBy { it.symbol }
                    SortType.LTP -> filtered.sortedByDescending { it.ltp }
                    SortType.QUANTITY -> filtered.sortedByDescending { it.quantity }
                    SortType.PNL -> filtered.sortedByDescending { (it.ltp - it.avgPrice) * it.quantity }
                }
            }
    }
    
    val portfolioSummary = remember(holdings) {
        calculatePortfolioSummary(holdings)
    }
    
    val rotationAngle by animateFloatAsState(
        targetValue = if (isSummaryExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "expand_rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    text = "Portfolio",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            actions = {
                IconButton(onClick = { showSortDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sort),
                        contentDescription = "Sort",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { showSearchDialog = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF1E40AF)
            )
        )

        // Tab Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(0.dp)
        ) {
            TabButton(
                text = "POSITIONS",
                isSelected = false,
                modifier = Modifier.weight(1f)
            )
            TabButton(
                text = "HOLDINGS",
                isSelected = false,
                modifier = Modifier.weight(1f)
            )
        }

        // Holdings List
        if (isLoading == true) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF1E40AF)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                state = rememberLazyListState(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                items(
                    items = filteredHoldings,
                    key = { holding -> holding.symbol },
                    contentType = { "holding_item" }
                ) { holding ->
                    HoldingItem(holding = holding)
                }
            }
        }

        // Portfolio Summary
        PortfolioSummaryCard(
            summary = portfolioSummary,
            isExpanded = isSummaryExpanded,
            onToggleExpansion = { isSummaryExpanded = !isSummaryExpanded },
            rotationAngle = rotationAngle
        )
    }

    // Sort Dialog
    if (showSortDialog) {
        SortDialog(
            currentSortType = currentSortType,
            onSortTypeSelected = {
                currentSortType = it
                showSortDialog = false
            },
            onDismiss = { showSortDialog = false }
        )
    }

    // Search Dialog
    if (showSearchDialog) {
        SearchDialog(
            searchQuery = searchQuery,
            onSearchQueryChanged = { searchQuery = it },
            onDismiss = { showSearchDialog = false }
        )
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { }
            .padding(vertical = 16.dp, horizontal = 0.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF1E40AF) else Color(0xFF6B7280),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Color(0xFF1E40AF))
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun HoldingItem(holding: UserHolding) {
    val pnl = remember(holding.ltp, holding.avgPrice, holding.quantity) {
        (holding.ltp - holding.avgPrice) * holding.quantity
    }
    val pnlColor = remember(pnl) {
        if (pnl >= 0) Color(0xFF059669) else Color(0xFFDC2626)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = holding.symbol,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = "LTP: ₹${String.format("%.2f", holding.ltp)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "NET QTY: ${holding.quantity}",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = "P&L: ₹${String.format("%.2f", pnl)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = pnlColor
                )
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE5E7EB))
        )
    }
}

@Composable
fun PortfolioSummaryCard(
    summary: PortfolioSummary,
    isExpanded: Boolean,
    onToggleExpansion: () -> Unit,
    rotationAngle: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F4F6))
    ) {
        // Expanded Content (shows ABOVE Profit & Loss when expanded)
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF3F4F6),
                        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .padding(16.dp)
            ) {
                SummaryRow("Current value", "₹${String.format("%.2f", summary.currentValue)}")
                Spacer(modifier = Modifier.height(16.dp))
                SummaryRow("Total investment*", "₹${String.format("%.2f", summary.totalInvestment)}")
                Spacer(modifier = Modifier.height(16.dp))
                SummaryRow(
                    "Today's P&L", 
                    if (summary.todaysPnL >= 0) {
                        "+₹${String.format("%.2f", summary.todaysPnL)}"
                    } else {
                        "₹${String.format("%.2f", summary.todaysPnL)}"
                    },
                    if (summary.todaysPnL >= 0) Color(0xFF059669) else Color(0xFFDC2626)
                )
            }
        }
        
        // Static Profit & Loss Row (ALWAYS at bottom)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpansion() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profit & Loss*",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_expand_up),
                    contentDescription = "Expand",
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(rotationAngle),
                    tint = Color(0xFF6B7280)
                )
            }
            
            Text(
                text = if (summary.totalPnL >= 0) {
                    "+₹${String.format("%.2f", summary.totalPnL)} (${String.format("%.2f", summary.totalPnLPercentage)}%)"
                } else {
                    "₹${String.format("%.2f", summary.totalPnL)} (${String.format("%.2f", summary.totalPnLPercentage)}%)"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (summary.totalPnL >= 0) Color(0xFF059669) else Color(0xFFDC2626)
            )
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF111827)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
fun SortDialog(
    currentSortType: SortType,
    onSortTypeSelected: (SortType) -> Unit,
    onDismiss: () -> Unit
) {
    val sortOptions = listOf(
        "Name (A-Z)" to SortType.NAME,
        "LTP (High to Low)" to SortType.LTP,
        "Quantity (High to Low)" to SortType.QUANTITY,
        "P&L (High to Low)" to SortType.PNL
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sort Holdings") },
        text = {
            Column {
                sortOptions.forEach { (title, sortType) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSortTypeSelected(sortType) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSortType == sortType,
                            onClick = { onSortTypeSelected(sortType) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = title)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SearchDialog(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf(searchQuery) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Search Holdings") },
        text = {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Enter symbol (e.g., RELIANCE, TCS)") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSearchQueryChanged(query)
                    onDismiss()
                }
            ) {
                Text("Search")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onSearchQueryChanged("")
                    onDismiss()
                }
            ) {
                Text("Clear")
            }
        }
    )
}

private fun calculatePortfolioSummary(holdings: List<UserHolding>): PortfolioSummary {
    var currentValue = 0.0
    var totalInvestment = 0.0
    var todaysPnL = 0.0

    holdings.forEach { holding ->
        currentValue += holding.ltp * holding.quantity
        totalInvestment += holding.avgPrice * holding.quantity
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

enum class SortType {
    NAME, LTP, QUANTITY, PNL
}
