package com.example.spamcallblocker_20235030.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spamcallblocker_20235030.data.SpamBlockerRepository
import com.example.spamcallblocker_20235030.util.PhoneNumberUtils
import com.example.spamcallblocker_20235030.util.RoleHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TestNumberResult(
    val checkedNumber: String,
    val normalizedNumber: String,
    val matchedBrand: String?,
    val isBlocked: Boolean
)

class SettingsViewModel(private val repository: SpamBlockerRepository) : ViewModel() {

    private val _isRoleGranted = MutableStateFlow(false)
    val isRoleGranted: StateFlow<Boolean> = _isRoleGranted.asStateFlow()

    private val _testResult = MutableStateFlow<TestNumberResult?>(null)
    val testResult: StateFlow<TestNumberResult?> = _testResult.asStateFlow()

    fun checkRoleStatus(context: Context) {
        _isRoleGranted.value = RoleHelper.isCallScreeningRoleHeld(context)
    }

    fun testPhoneNumber(inputNumber: String) {
        if (inputNumber.isBlank()) {
            _testResult.value = null
            return
        }

        viewModelScope.launch {
            val normalized = PhoneNumberUtils.normalize(inputNumber)
            val matchedBrand = repository.findActiveBrandForNumber(inputNumber)

            _testResult.value = TestNumberResult(
                checkedNumber = inputNumber,
                normalizedNumber = normalized,
                matchedBrand = matchedBrand,
                isBlocked = matchedBrand != null
            )
        }
    }

    fun clearTestResult() {
        _testResult.value = null
    }
}
