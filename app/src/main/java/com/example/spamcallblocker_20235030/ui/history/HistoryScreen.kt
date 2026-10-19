package com.example.spamcallblocker_20235030.ui.history

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhoneDisabled
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spamcallblocker_20235030.data.entity.BlockedCallLogEntity
import com.example.spamcallblocker_20235030.ui.components.ConfirmDialog
import com.example.spamcallblocker_20235030.ui.components.EmptyState
import com.example.spamcallblocker_20235030.util.PhoneNumberUtils
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            placeholder = { Text("Tìm theo số hoặc thương hiệu...") },
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
                            text = "Lịch Sử Chặn Cuộc Gọi",
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
                    if (uiState.logs.isNotEmpty()) {
                        IconButton(onClick = { showClearConfirmDialog = true }) {
                            Icon(
                                Icons.Default.DeleteSweep,
                                contentDescription = "Xóa toàn bộ lịch sử",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.logs.isEmpty()) {
                EmptyState(
                    title = if (uiState.searchQuery.isNotBlank()) "Không tìm thấy lịch sử" else "Chưa có cuộc gọi bị chặn",
                    message = if (uiState.searchQuery.isNotBlank()) "Thử tìm kiếm từ khóa khác" else "Khi ứng dụng từ chối cuộc gọi spam, lịch sử sẽ xuất hiện tại đây",
                    icon = Icons.Default.History,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.logs,
                        key = { it.id }
                    ) { log ->
                        HistoryItemCard(
                            log = log,
                            onDelete = { viewModel.deleteLog(log) }
                        )
                    }
                }
            }
        }
    }

    if (showClearConfirmDialog) {
        ConfirmDialog(
            title = "Xóa toàn bộ lịch sử",
            message = "Bạn có chắc chắn muốn xóa toàn bộ danh sách cuộc gọi đã bị chặn?",
            confirmText = "Xóa Tất Cả",
            onConfirm = {
                viewModel.clearAllLogs()
                showClearConfirmDialog = false
            },
            onDismiss = { showClearConfirmDialog = false }
        )
    }
}

@Composable
fun HistoryItemCard(
    log: BlockedCallLogEntity,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val formattedTime = remember(log.timestamp) {
        DateFormat.getDateFormat(context).format(Date(log.timestamp)) + " " +
                DateFormat.getTimeFormat(context).format(Date(log.timestamp))
    }

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
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(22.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneDisabled,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = PhoneNumberUtils.formatForDisplay(log.phoneNumber),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Thương hiệu: ${log.brandName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Thời gian: $formattedTime",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (log.reason.isNotBlank()) {
                    Text(
                        text = log.reason,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Xóa dòng này",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
