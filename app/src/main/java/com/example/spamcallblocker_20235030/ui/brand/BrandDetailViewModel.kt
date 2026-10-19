package com.example.spamcallblocker_20235030.ui.brand

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spamcallblocker_20235030.data.SpamBlockerRepository
import com.example.spamcallblocker_20235030.data.entity.BrandEntity
import com.example.spamcallblocker_20235030.data.entity.PhoneNumberEntity
import com.example.spamcallblocker_20235030.data.relation.BrandWithNumbers
import com.example.spamcallblocker_20235030.util.PhoneNumberUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BrandDetailUiState(
    val brandWithNumbers: BrandWithNumbers? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class BrandDetailViewModel(
    private val repository: SpamBlockerRepository,
    private val brandId: Long
) : ViewModel() {

    val uiState: StateFlow<BrandDetailUiState> = repository.getBrandWithNumbersByIdFlow(brandId)
        .map { brandWithNumbers ->
            BrandDetailUiState(
                brandWithNumbers = brandWithNumbers,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BrandDetailUiState(isLoading = true)
        )

    fun toggleBrandEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.setBrandEnabled(brandId, isEnabled)
        }
    }

    fun updateBrandInfo(newName: String, newDescription: String, onResult: (Boolean, String?) -> Unit) {
        if (newName.isBlank()) {
            onResult(false, "Tên thương hiệu không được để trống")
            return
        }

        viewModelScope.launch {
            if (repository.isBrandNameExists(newName, excludeId = brandId)) {
                onResult(false, "Tên thương hiệu '$newName' đã trùng với thương hiệu khác")
            } else {
                val currentBrand = uiState.value.brandWithNumbers?.brand
                if (currentBrand != null) {
                    repository.updateBrand(currentBrand.copy(name = newName, description = newDescription))
                    onResult(true, null)
                } else {
                    onResult(false, "Không tìm thấy dữ liệu thương hiệu")
                }
            }
        }
    }

    fun addPhoneNumber(rawNumber: String, note: String, onResult: (Boolean, String?) -> Unit) {
        if (rawNumber.isBlank()) {
            onResult(false, "Số điện thoại không được để trống")
            return
        }

        if (!PhoneNumberUtils.isValidPhoneNumber(rawNumber)) {
            onResult(false, "Số điện thoại không hợp lệ (cần từ 9 - 11 chữ số)")
            return
        }

        viewModelScope.launch {
            if (repository.isNormalizedNumberExists(rawNumber)) {
                onResult(false, "Số điện thoại '$rawNumber' đã tồn tại trong danh sách")
            } else {
                repository.addPhoneNumber(brandId = brandId, rawNumber = rawNumber, note = note)
                onResult(true, null)
            }
        }
    }

    fun updatePhoneNumber(entity: PhoneNumberEntity, newRawNumber: String, newNote: String, onResult: (Boolean, String?) -> Unit) {
        if (newRawNumber.isBlank()) {
            onResult(false, "Số điện thoại không được để trống")
            return
        }

        if (!PhoneNumberUtils.isValidPhoneNumber(newRawNumber)) {
            onResult(false, "Số điện thoại không hợp lệ (cần từ 9 - 11 chữ số)")
            return
        }

        viewModelScope.launch {
            if (repository.isNormalizedNumberExists(newRawNumber, excludeId = entity.id)) {
                onResult(false, "Số điện thoại '$newRawNumber' đã tồn tại trong danh sách")
            } else {
                repository.updatePhoneNumber(entity, newRawNumber, newNote)
                onResult(true, null)
            }
        }
    }

    fun deletePhoneNumber(entity: PhoneNumberEntity) {
        viewModelScope.launch {
            repository.deletePhoneNumber(entity)
        }
    }
}
