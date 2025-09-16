package com.sagarpaliwal_task.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarpaliwal_task.core.domain.GetHoldingsUseCase
import com.sagarpaliwal_task.core.model.HoldingsResponse
import kotlinx.coroutines.launch

class MainViewModel(
    private val getHoldingsUseCase: GetHoldingsUseCase
) : ViewModel() {
    
    private val _holdingsResult = MutableLiveData<Result<HoldingsResponse>?>()
    val holdingsResult: LiveData<Result<HoldingsResponse>?> = _holdingsResult
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun loadHoldings() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = getHoldingsUseCase()
                _holdingsResult.value = result
            } catch (e: Exception) {
                _holdingsResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

