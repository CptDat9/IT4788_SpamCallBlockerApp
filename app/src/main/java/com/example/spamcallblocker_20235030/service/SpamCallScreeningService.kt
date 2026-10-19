package com.example.spamcallblocker_20235030.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.example.spamcallblocker_20235030.SpamBlockerApp
import com.example.spamcallblocker_20235030.util.PhoneNumberUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpamCallScreeningService : CallScreeningService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onScreenCall(callDetails: Call.Details) {
        val rawHandle = callDetails.handle?.schemeSpecificPart ?: ""
        val normalizedCaller = PhoneNumberUtils.normalize(rawHandle)

        Log.d(TAG, "Nhận cuộc gọi đến: raw=$rawHandle, normalized=$normalizedCaller")

        serviceScope.launch {
            try {
                val repository = SpamBlockerApp.instance.repository
                val brandName = repository.findActiveBrandForNumber(normalizedCaller)

                if (brandName != null) {
                    Log.w(TAG, "CHẶN CUỘC GỌI SPAM: $normalizedCaller thuộc thương hiệu '$brandName'")

                    // Từ chối cuộc gọi theo API Android
                    val response = CallResponse.Builder()
                        .setDisallowCall(true)
                        .setRejectCall(true)
                        .setSkipCallLog(false)
                        .setSkipNotification(true)
                        .build()

                    // Lưu lịch sử chặn
                    repository.addBlockedLog(
                        phoneNumber = if (rawHandle.isNotBlank()) rawHandle else normalizedCaller,
                        brandName = brandName,
                        reason = "Tự động chặn cuộc gọi từ thương hiệu $brandName"
                    )

                    respondToCall(callDetails, response)
                } else {
                    Log.d(TAG, "CHO PHÉP CUỘC GỌI: $normalizedCaller không nằm trong danh sách đen")

                    val response = CallResponse.Builder()
                        .setDisallowCall(false)
                        .build()

                    respondToCall(callDetails, response)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi xử lý sàng lọc cuộc gọi", e)
                // Fallback an toàn: Cho phép cuộc gọi bình thường nếu có lỗi hệ thống/database
                try {
                    val response = CallResponse.Builder()
                        .setDisallowCall(false)
                        .build()
                    respondToCall(callDetails, response)
                } catch (ignored: Exception) {
                }
            }
        }
    }

    companion object {
        private const val TAG = "SpamCallScreening"
    }
}
