package com.example.spamcallblocker_20235030.ui.dashboard

import android.app.Activity
import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneDisabled
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spamcallblocker_20235030.data.entity.BlockedCallLogEntity
import com.example.spamcallblocker_20235030.ui.components.EmptyState
import com.example.spamcallblocker_20235030.util.PhoneNumberUtils
import com.example.spamcallblocker_20235030.util.RoleHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToBrands: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkRoleStatus(context)
    }

    val roleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        viewModel.checkRoleStatus(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Trạng Thái Bảo Vệ",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Banner
            item {
                ProtectionStatusCard(
                    isRoleGranted = uiState.isRoleGranted,
                    onRequestRole = {
                        val intent = RoleHelper.createRequestRoleIntent(context)
                        if (intent != null) {
                            roleLauncher.launch(intent)
                        } else {
                            onNavigateToSettings()
                        }
                    }
                )
            }

            // Statistics Grid
            item {
                Text(
                    text = "Thống Kê Hoạt Động",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Thương hiệu",
                        value = uiState.totalBrands.toString(),
                        icon = Icons.Default.Business,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToBrands
                    )
                    StatCard(
                        title = "Số điện thoại",
                        value = uiState.totalNumbers.toString(),
                        icon = Icons.Default.Phone,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToBrands
                    )
                    StatCard(
                        title = "Đã chặn",
                        value = uiState.totalBlockedCalls.toString(),
                        icon = Icons.Default.PhoneDisabled,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToHistory
                    )
                }
            }

            // Recent Blocked Calls Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cuộc Gọi Bị Chặn Gần Đây",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (uiState.recentLogs.isNotEmpty()) {
                        TextButton(onClick = onNavigateToHistory) {
                            Text(text = "Xem tất cả")
                        }
                    }
                }
            }

            if (uiState.recentLogs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        EmptyState(
                            title = "Chưa có cuộc gọi bị chặn",
                            message = "Các cuộc gọi đến thuộc thương hiệu spam sẽ xuất hiện ở đây",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(uiState.recentLogs) { log ->
                    RecentBlockedItem(log = log)
                }
            }
        }
    }
}

@Composable
fun ProtectionStatusCard(
    isRoleGranted: Boolean,
    onRequestRole: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isRoleGranted) {
                Color(0xFFE8F5E9)
            } else {
                Color(0xFFFFF3E0)
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isRoleGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isRoleGranted) Color(0xFF2E7D32) else Color(0xFFE65100),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isRoleGranted) "Đang Bảo Vệ Cuộc Gọi" else "Chưa Cấp Quyền Sàng Lọc",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isRoleGranted) Color(0xFF1B5E20) else Color(0xFFE65100)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isRoleGranted) {
                        "Ứng dụng đã sẵn sàng chặn các cuộc gọi spam tự động"
                    } else {
                        "Nhấn vào đây để bật vai trò Sàng lọc cuộc gọi (Call Screening Role)"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isRoleGranted) Color(0xFF2E7D32) else Color(0xFFBF360C)
                )
                if (!isRoleGranted) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onRequestRole,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE65100)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "Cấp Quyền Ngay", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RecentBlockedItem(log: BlockedCallLogEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneDisabled,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
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
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = DateUtils.getRelativeTimeSpanString(log.timestamp).toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
