package com.example.spamcallblocker_20235030.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spamcallblocker_20235030.util.PhoneNumberUtils
import com.example.spamcallblocker_20235030.util.RoleHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val isRoleGranted by viewModel.isRoleGranted.collectAsState()
    val testResult by viewModel.testResult.collectAsState()

    var testInputNumber by remember { mutableStateOf("") }

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
                        text = "Cài Đặt & Cấp Quyền",
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
            // Permission Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isRoleGranted) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isRoleGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isRoleGranted) Color(0xFF2E7D32) else Color(0xFFE65100),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isRoleGranted) "Quyền Sàng Lọc: ĐÃ BẬT" else "Quyền Sàng Lọc: CHƯA BẬT",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRoleGranted) Color(0xFF1B5E20) else Color(0xFFE65100)
                                )
                                Text(
                                    text = if (isRoleGranted) "CallScreeningService đang hoạt động bình thường" else "Cần cấp quyền để Android cho phép ứng dụng chặn cuộc gọi",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isRoleGranted) Color(0xFF2E7D32) else Color(0xFFBF360C)
                                )
                            }
                        }

                        if (!isRoleGranted) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val intent = RoleHelper.createRequestRoleIntent(context)
                                    if (intent != null) {
                                        roleLauncher.launch(intent)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                            ) {
                                Text("Cấp Vai Trò Sàng Lọc Cuộc Gọi")
                            }
                        }
                    }
                }
            }

            // Test Number Checker Tool
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Công Cụ Kiểm Tra Nhận Diện Số",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Nhập một số điện thoại bất kỳ để kiểm tra xem hệ thống sẽ CHẶN hay CHO PHÉP cuộc gọi này.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = testInputNumber,
                                onValueChange = { testInputNumber = it },
                                placeholder = { Text("VD: 0901234567...") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { viewModel.testPhoneNumber(testInputNumber) }
                            ) {
                                Text("Kiểm tra")
                            }
                        }

                        AnimatedVisibility(visible = testResult != null) {
                            testResult?.let { result ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (result.isBlocked) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = if (result.isBlocked) "Kết quả: BỊ CHẶN 🚫" else "Kết quả: CHO PHÉP (AN TOÀN) ✅",
                                            fontWeight = FontWeight.Bold,
                                            color = if (result.isBlocked) Color(0xFFC62828) else Color(0xFF2E7D32)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Số nhập: ${result.checkedNumber} • Số chuẩn hóa: ${result.normalizedNumber}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        if (result.matchedBrand != null) {
                                            Text(
                                                text = "Thương hiệu phát hiện: ${result.matchedBrand}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFC62828)
                                            )
                                        } else {
                                            Text(
                                                text = "Số này chưa có trong danh sách đen của thương hiệu nào đang bật.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF2E7D32)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // User Instructions
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hướng Dẫn & Lưu Ý",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "1. Nhấn nút 'Cấp Vai Trò' ở trên để Android gán ứng dụng làm Call Screening App mặc định.\n" +
                                    "2. Thêm các thương hiệu và số điện thoại cần chặn vào tab 'Thương hiệu'.\n" +
                                    "3. Khi có cuộc gọi đến từ số điện thoại đã gán, ứng dụng sẽ tự động từ chối và ghi lịch sử.\n" +
                                    "4. Các số điện thoại được tự động chuẩn hóa (VD: +84901234567, 0901-234-567 -> 0901234567) để đối chiếu chính xác.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // App Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ứng Dụng Chặn Cuộc Gọi Spam",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Phiên bản 1.0.0 • Package: com.example.spamcallblocker_20235030",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Bài Tập Đại Học - Android Native Jetpack Compose & Room",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
