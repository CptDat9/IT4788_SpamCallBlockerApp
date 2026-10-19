package com.example.spamcallblocker_20235030.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spamcallblocker_20235030.data.SpamBlockerRepository
import com.example.spamcallblocker_20235030.data.entity.BlockedCallLogEntity
import com.example.spamcallblocker_20235030.util.RoleHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val totalBrands: Int = 0,
    val totalNumbers: Int = 0,
    val totalBlockedCalls: Int = 0,
    val isRoleGranted: Boolean = false,
    val recentLogs: List<BlockedCallLogEntity> = emptyList(),
    val isLoading: Boolean = false
)

class DashboardViewModel(private val repository: SpamBlockerRepository) : ViewModel() {

    private val _isRoleGranted = MutableStateFlow(false)

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.totalBrandsCount,
        repository.totalPhoneNumbersCount,
        repository.totalBlockedCount,
        _isRoleGranted,
        repository.recentLogsFlow
    ) { brands, numbers, blocked, roleGranted, logs ->
        DashboardUiState(
            totalBrands = brands,
            totalNumbers = numbers,
            totalBlockedCalls = blocked,
            isRoleGranted = roleGranted,
            recentLogs = logs,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    fun checkRoleStatus(context: Context) {
        _isRoleGranted.value = RoleHelper.isCallScreeningRoleHeld(context)
    }
}
