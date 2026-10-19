package com.example.spamcallblocker_20235030.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spamcallblocker_20235030.data.SpamBlockerRepository
import com.example.spamcallblocker_20235030.data.entity.BlockedCallLogEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HistoryUiState(
    val logs: List<BlockedCallLogEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class HistoryViewModel(private val repository: SpamBlockerRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<HistoryUiState> = combine(
        repository.allLogsFlow,
        _searchQuery
    ) { logs, query ->
        val filtered = if (query.isBlank()) {
            logs
        } else {
            logs.filter {
                it.phoneNumber.contains(query, ignoreCase = true) ||
                        it.brandName.contains(query, ignoreCase = true) ||
                        it.reason.contains(query, ignoreCase = true)
            }
        }
        HistoryUiState(
            logs = filtered,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(isLoading = true)
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun deleteLog(log: BlockedCallLogEntity) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearAllLogs()
        }
    }
}
