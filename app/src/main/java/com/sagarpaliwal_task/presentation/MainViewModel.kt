package com.sagarpaliwal_task.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagarpaliwal_task.core.domain.GetHoldingsUseCase
import com.sagarpaliwal_task.core.model.HoldingsResponse
import com.sagarpaliwal_task.core.util.Failure
import kotlinx.coroutines.launch

class MainViewModel(
    private val getHoldingsUseCase: GetHoldingsUseCase
) : ViewModel() {

    private val _loader = MutableLiveData<Boolean>()
    val loader: LiveData<Boolean> = _loader

    private val _holdingsData = MutableLiveData<HoldingsResponse>()
    val holdingsData: LiveData<HoldingsResponse> = _holdingsData

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun loadHoldings() {
        _loader.value = true
        viewModelScope.launch {
            getHoldingsUseCase().fold(::handleFailure, ::handleSuccess)
        }
    }

    private fun handleSuccess(holdingsResponse: HoldingsResponse) {
        _loader.value = false
        _holdingsData.value = holdingsResponse
    }

    private fun handleFailure(failure: Failure) {
        _loader.value = false
        _errorMessage.value = when (failure) {
            is Failure.NetworkConnection -> "Network connection error"
            is Failure.ServerError -> "Server error occurred"
            is Failure.UnauthorizedError -> "Unauthorized access"
            is Failure.UnknownError -> failure.message
        }
    }
}