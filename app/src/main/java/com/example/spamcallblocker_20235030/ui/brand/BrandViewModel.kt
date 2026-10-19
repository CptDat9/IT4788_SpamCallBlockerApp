package com.example.spamcallblocker_20235030.ui.brand

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spamcallblocker_20235030.data.SpamBlockerRepository
import com.example.spamcallblocker_20235030.data.entity.BrandEntity
import com.example.spamcallblocker_20235030.data.relation.BrandWithNumbers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BrandListUiState(
    val brandsWithNumbers: List<BrandWithNumbers> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class BrandViewModel(private val repository: SpamBlockerRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<BrandListUiState> = combine(
        repository.allBrandsWithNumbersFlow,
        _searchQuery,
        _errorMessage
    ) { brands, query, error ->
        val filtered = if (query.isBlank()) {
            brands
        } else {
            brands.filter { b ->
                b.brand.name.contains(query, ignoreCase = true) ||
                        b.brand.description.contains(query, ignoreCase = true) ||
                        b.phoneNumbers.any { p -> p.rawNumber.contains(query) || p.normalizedNumber.contains(query) }
            }
        }
        BrandListUiState(
            brandsWithNumbers = filtered,
            searchQuery = query,
            isLoading = false,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BrandListUiState(isLoading = true)
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun toggleBrandEnabled(brandId: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.setBrandEnabled(brandId, isEnabled)
        }
    }

    fun addBrand(name: String, description: String, onResult: (Boolean, String?) -> Unit) {
        if (name.isBlank()) {
            onResult(false, "Tên thương hiệu không được để trống")
            return
        }

        viewModelScope.launch {
            if (repository.isBrandNameExists(name)) {
                onResult(false, "Tên thương hiệu '$name' đã tồn tại")
            } else {
                repository.insertBrand(name = name, description = description, isEnabled = true)
                onResult(true, null)
            }
        }
    }

    fun deleteBrand(brand: BrandEntity) {
        viewModelScope.launch {
            repository.deleteBrand(brand)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
