package com.example.spamcallblocker_20235030.util

object PhoneNumberUtils {

    /**
     * Chuẩn hóa số điện thoại về dạng số thuần túy (VD: "0912345678").
     * - Loại bỏ khoảng trắng, dấu gạch ngang, dấu ngoặc, dấu cộng.
     * - Đổi mã quốc gia Việt Nam (+84 hoặc 84) ở đầu thành số '0'.
     */
    fun normalize(rawNumber: String?): String {
        if (rawNumber.isNullOrBlank()) return ""
        val trimmed = rawNumber.trim()

        // Loại bỏ protocol "tel:" nếu có từ Android CallDetails
        var cleaned = if (trimmed.startsWith("tel:", ignoreCase = true)) {
            trimmed.substring(4)
        } else {
            trimmed
        }

        // Loại bỏ các ký tự không phải là chữ số ngoại trừ dấu + ở đầu
        cleaned = cleaned.replace(Regex("[^0-9+]"), "")

        // Xử lý mã quốc gia Việt Nam
        return when {
            cleaned.startsWith("+84") -> "0" + cleaned.substring(3)
            cleaned.startsWith("84") && cleaned.length >= 10 -> "0" + cleaned.substring(2)
            cleaned.startsWith("+") -> cleaned.substring(1)
            else -> cleaned
        }
    }

    /**
     * Định dạng số điện thoại hiển thị đẹp mắt (VD: "0912 345 678" hoặc "024 3999 8888").
     */
    fun formatForDisplay(rawNumber: String): String {
        val normalized = normalize(rawNumber)
        if (normalized.length == 10 && normalized.startsWith("0")) {
            return "${normalized.substring(0, 4)} ${normalized.substring(4, 7)} ${normalized.substring(7)}"
        } else if (normalized.length == 11 && normalized.startsWith("02")) { // Số bàn Hà Nội/TP.HCM
            return "${normalized.substring(0, 3)} ${normalized.substring(3, 7)} ${normalized.substring(7)}"
        }
        return rawNumber
    }

    /**
     * Kiểm tra số điện thoại có hợp lệ không.
     */
    fun isValidPhoneNumber(rawNumber: String): Boolean {
        val normalized = normalize(rawNumber)
        // Chấp nhận số điện thoại Việt Nam từ 9 đến 11 chữ số
        return normalized.length in 9..11 && normalized.all { it.isDigit() }
    }
}
