package com.example.spamcallblocker_20235030.ui.brand

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spamcallblocker_20235030.data.entity.PhoneNumberEntity
import com.example.spamcallblocker_20235030.ui.components.ConfirmDialog
import com.example.spamcallblocker_20235030.ui.components.EmptyState
import com.example.spamcallblocker_20235030.util.PhoneNumberUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandDetailScreen(
    viewModel: BrandDetailViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val brandWithNumbers = uiState.brandWithNumbers

    var showEditBrandDialog by remember { mutableStateOf(false) }
    var showAddPhoneDialog by remember { mutableStateOf(false) }
    var phoneToEdit by remember { mutableStateOf<PhoneNumberEntity?>(null) }
    var phoneToDelete by remember { mutableStateOf<PhoneNumberEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = brandWithNumbers?.brand?.name ?: "Chi Tiết Thương Hiệu",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    if (brandWithNumbers != null) {
                        IconButton(onClick = { showEditBrandDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Sửa thông tin")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        floatingActionButton = {
            if (brandWithNumbers != null) {
                FloatingActionButton(
                    onClick = { showAddPhoneDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm số điện thoại")
                }
            }
        }
    ) { paddingValues ->
        if (brandWithNumbers == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val brand = brandWithNumbers.brand
            val numbers = brandWithNumbers.phoneNumbers

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Info Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = brand.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Switch(
                                    checked = brand.isEnabled,
                                    onCheckedChange = { viewModel.toggleBrandEnabled(it) }
                                )
                            }

                            if (brand.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = brand.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Trạng thái: ${if (brand.isEnabled) "ĐANG CHẶN" else "TẮT CHẶN"} • Tổng cộng: ${numbers.size} số điện thoại",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Danh Sách Số Điện Thoại (${numbers.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (numbers.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            EmptyState(
                                title = "Chưa có số điện thoại nào",
                                message = "Nhấn nút + góc dưới để thêm số điện thoại cho thương hiệu này",
                                icon = Icons.Default.Phone,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(
                        items = numbers,
                        key = { it.id }
                    ) { phoneEntity ->
                        PhoneNumberItem(
                            phoneEntity = phoneEntity,
                            onEdit = { phoneToEdit = phoneEntity },
                            onDelete = { phoneToDelete = phoneEntity }
                        )
                    }
                }
            }
        }
    }

    // Edit Brand Dialog
    if (showEditBrandDialog && brandWithNumbers != null) {
        EditBrandDialog(
            currentName = brandWithNumbers.brand.name,
            currentDescription = brandWithNumbers.brand.description,
            onDismiss = { showEditBrandDialog = false },
            onConfirm = { name, description, onError ->
                viewModel.updateBrandInfo(name, description) { success, err ->
                    if (success) {
                        showEditBrandDialog = false
                    } else {
                        onError(err ?: "Lỗi cập nhật thương hiệu")
                    }
                }
            }
        )
    }

    // Add Phone Number Dialog
    if (showAddPhoneDialog) {
        PhoneEditorDialog(
            title = "Thêm Số Điện Thoại Mới",
            confirmButtonText = "Thêm",
            initialNumber = "",
            initialNote = "",
            onDismiss = { showAddPhoneDialog = false },
            onConfirm = { rawNum, note, onError ->
                viewModel.addPhoneNumber(rawNum, note) { success, err ->
                    if (success) {
                        showAddPhoneDialog = false
                    } else {
                        onError(err ?: "Đã có lỗi xảy ra")
                    }
                }
            }
        )
    }

    // Edit Phone Number Dialog
    phoneToEdit?.let { entity ->
        PhoneEditorDialog(
            title = "Sửa Số Điện Thoại",
            confirmButtonText = "Lưu Thay Đổi",
            initialNumber = entity.rawNumber,
            initialNote = entity.note,
            onDismiss = { phoneToEdit = null },
            onConfirm = { newRawNum, newNote, onError ->
                viewModel.updatePhoneNumber(entity, newRawNum, newNote) { success, err ->
                    if (success) {
                        phoneToEdit = null
                    } else {
                        onError(err ?: "Đã có lỗi xảy ra")
                    }
                }
            }
        )
    }

    // Delete Phone Confirmation
    phoneToDelete?.let { entity ->
        ConfirmDialog(
            title = "Xác nhận xóa số điện thoại",
            message = "Bạn có chắc chắn muốn xóa số '${PhoneNumberUtils.formatForDisplay(entity.rawNumber)}' khỏi thương hiệu này?",
            onConfirm = {
                viewModel.deletePhoneNumber(entity)
                phoneToDelete = null
            },
            onDismiss = { phoneToDelete = null }
        )
    }
}

@Composable
fun PhoneNumberItem(
    phoneEntity: PhoneNumberEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = PhoneNumberUtils.formatForDisplay(phoneEntity.rawNumber),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Số chuẩn hóa: ${phoneEntity.normalizedNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (phoneEntity.note.isNotBlank()) {
                    Text(
                        text = "Ghi chú: ${phoneEntity.note}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Sửa số")
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Xóa số",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EditBrandDialog(
    currentName: String,
    currentDescription: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, onError: (String) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var description by remember { mutableStateOf(currentDescription) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chỉnh Sửa Thương Hiệu") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        errorMessage = null
                    },
                    label = { Text("Tên thương hiệu (*)") },
                    singleLine = true,
                    isError = errorMessage != null,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả / Ghi chú") },
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
            Button(onClick = { onConfirm(name, description) { err -> errorMessage = err } }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun PhoneEditorDialog(
    title: String,
    confirmButtonText: String,
    initialNumber: String,
    initialNote: String,
    onDismiss: () -> Unit,
    onConfirm: (rawNumber: String, note: String, onError: (String) -> Unit) -> Unit
) {
    var rawNumber by remember { mutableStateOf(initialNumber) }
    var note by remember { mutableStateOf(initialNote) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = rawNumber,
                    onValueChange = {
                        rawNumber = it
                        errorMessage = null
                    },
                    label = { Text("Số điện thoại (*)") },
                    placeholder = { Text("VD: 0912345678, +84 912 345 678...") },
                    singleLine = true,
                    isError = errorMessage != null,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ghi chú (Tùy chọn)") },
                    placeholder = { Text("VD: Số máy bàn chào mời, số tổng đài...") },
                    singleLine = true,
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
            Button(onClick = { onConfirm(rawNumber, note) { err -> errorMessage = err } }) {
                Text(text = confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}
