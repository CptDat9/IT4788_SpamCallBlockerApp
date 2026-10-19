package com.example.spamcallblocker_20235030.ui.brand

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spamcallblocker_20235030.data.entity.BrandEntity
import com.example.spamcallblocker_20235030.data.relation.BrandWithNumbers
import com.example.spamcallblocker_20235030.ui.components.ConfirmDialog
import com.example.spamcallblocker_20235030.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandListScreen(
    viewModel: BrandViewModel,
    onBrandClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var brandToDelete by remember { mutableStateOf<BrandEntity?>(null) }
    var isSearching by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            placeholder = { Text("Tìm thương hiệu hoặc số đt...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    viewModel.onSearchQueryChange("")
                                    isSearching = false
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Đóng")
                                }
                            }
                        )
                    } else {
                        Text(
                            text = "Danh Sách Thương Hiệu",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    if (!isSearching) {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Tìm kiếm")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm thương hiệu")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.brandsWithNumbers.isEmpty()) {
                EmptyState(
                    title = if (uiState.searchQuery.isNotBlank()) "Không tìm thấy kết quả" else "Chưa có thương hiệu nào",
                    message = if (uiState.searchQuery.isNotBlank()) "Thử tìm kiếm với từ khóa khác" else "Nhấn nút + bên dưới để thêm thương hiệu cần chặn",
                    icon = Icons.Default.Business,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.brandsWithNumbers,
                        key = { it.brand.id }
                    ) { item ->
                        BrandCardItem(
                            brandWithNumbers = item,
                            onClick = { onBrandClick(item.brand.id) },
                            onToggleEnabled = { isEnabled ->
                                viewModel.toggleBrandEnabled(item.brand.id, isEnabled)
                            },
                            onDelete = { brandToDelete = item.brand }
                        )
                    }
                }
            }
        }
    }

    // Add Brand Dialog
    if (showAddDialog) {
        AddBrandDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, description, onError ->
                viewModel.addBrand(name, description) { success, errorMsg ->
                    if (success) {
                        showAddDialog = false
                    } else {
                        onError(errorMsg ?: "Đã có lỗi xảy ra")
                    }
                }
            }
        )
    }

    // Delete Confirmation Dialog
    brandToDelete?.let { brand ->
        ConfirmDialog(
            title = "Xác nhận xóa",
            message = "Bạn có chắc chắn muốn xóa thương hiệu '${brand.name}' cùng tất cả các số điện thoại đi kèm?",
            onConfirm = {
                viewModel.deleteBrand(brand)
                brandToDelete = null
            },
            onDismiss = { brandToDelete = null }
        )
    }
}

@Composable
fun BrandCardItem(
    brandWithNumbers: BrandWithNumbers,
    onClick: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val brand = brandWithNumbers.brand
    val count = brandWithNumbers.phoneNumbers.size

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (brand.isEnabled) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = brand.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (brand.isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "$count số")
                        }
                    }
                }

                if (brand.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = brand.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Switch(
                checked = brand.isEnabled,
                onCheckedChange = onToggleEnabled
            )

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa thương hiệu",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun AddBrandDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, onError: (String) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Thêm Thương Hiệu Mới") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Tên thương hiệu (*)") },
                    placeholder = { Text("VD: FE Credit, Shinhan Bank...") },
                    singleLine = true,
                    isError = errorMessage != null,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả / Ghi chú") },
                    placeholder = { Text("VD: Gọi điện vay vốn, spam tài chính...") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(name, description) { err ->
                        errorMessage = err
                    }
                }
            ) {
                Text(text = "Thêm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Hủy")
            }
        }
    )
}
