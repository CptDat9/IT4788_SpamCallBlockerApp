# Ứng Dụng Chặn Cuộc Gọi Spam (Spam Call Blocker)

Ứng dụng Android Native giúp nhận diện và tự động chặn các cuộc gọi spam dựa trên danh sách thương hiệu và số điện thoại được quản lý linh hoạt.

---

## 🌟 Tính Năng Chính

### 1. Quản Lý Thương Hiệu & Số Điện Thoại
* **Thêm, sửa, xóa thương hiệu**: Cho phép tạo các thương hiệu/dịch vụ hay thực hiện cuộc gọi spam (VD: FE Credit, Shinhan Bank, Bất Động Sản...).
* **Bật/Tắt chặn theo từng thương hiệu**: Dễ dàng bật hoặc tắt chặn cho một thương hiệu cụ thể mà không cần xóa dữ liệu.
* **Quản lý danh sách số điện thoại**: Một thương hiệu có thể chứa nhiều số điện thoại khác nhau. Hỗ trợ thêm, sửa, xóa từng số.
* **Chuẩn hóa số điện thoại tự động**: Tự động loại bỏ khoảng trắng, dấu gạch ngang, ký tự đặc biệt và quy đổi mã quốc gia Việt Nam (`+84`, `84` -> `0`) để so khớp 100% chính xác.
* **Kiểm tra dữ liệu đầu vào**: Ràng buộc không cho phép nhập dữ liệu rỗng, số điện thoại không hợp lệ (cần từ 9 - 11 chữ số) hoặc trùng lặp dữ liệu.

### 2. Chặn Cuộc Gọi Thực Tế (CallScreeningService)
* Triển khai chuẩn **Android CallScreeningService API** (`android.telecom.CallScreeningService`).
* **Sàng lọc tự động thời gian thực**: Đối chiếu số điện thoại cuộc gọi đến với danh sách đen Room Database ngay khi cuộc gọi bắt đầu.
* **Tự động từ chối cuộc gọi**: Nếu số điện thoại thuộc thương hiệu đang BẬT chặn, cuộc gọi lập tức bị từ chối (`setDisallowCall(true)`, `setRejectCall(true)`) và không hiển thị thông báo làm phiền.
* **An toàn & không làm gián đoạn**: Xử lý bất đồng bộ trên `Dispatchers.IO`, có fallback tự động cho phép cuộc gọi nếu có lỗi hệ thống, không làm gián đoạn luồng cuộc gọi và không gây crash app.
* **Yêu cầu vai trò hệ thống**: Tích hợp API `RoleManager` chính thức để hướng dẫn người dùng cấp quyền vai trò Sàng lọc cuộc gọi.

### 3. Lịch Sử Chặn Cuộc Gọi
* Ghi nhận đầy đủ lịch sử các cuộc gọi bị chặn: Số điện thoại, Tên thương hiệu, Thời gian chặn, Lý do chặn.
* Tìm kiếm lịch sử chặn theo số điện thoại hoặc tên thương hiệu.
* Hỗ trợ xóa từng dòng lịch sử hoặc xóa toàn bộ lịch sử có hộp thoại xác nhận.

### 4. Giao Diện Người Dùng (Material 3 & Jetpack Compose)
* **Dashboard (Tổng quan)**: Hiển thị trạng thái bảo vệ (BẬT/TẮT vai trò sàng lọc), thống kê thực tế (Tổng thương hiệu, tổng số điện thoại, tổng cuộc gọi đã chặn) và danh sách cuộc gọi bị chặn gần đây.
* **Màn hình quản lý thương hiệu**: Danh sách thẻ thương hiệu với công tắc Bật/Tắt nhanh, thanh tìm kiếm và nút thêm thương hiệu mới.
* **Màn hình chi tiết thương hiệu**: Xem và chỉnh sửa thông tin thương hiệu, quản lý danh sách các số điện thoại đi kèm.
* **Màn hình cài đặt & Cấp quyền**: Cấp quyền `CallScreeningService`, tích hợp **Công cụ kiểm tra nhanh số điện thoại** (giúp thử nghiệm số điện thoại bất kỳ có bị chặn hay không), kèm hướng dẫn sử dụng chi tiết.

---

## 🛠️ Công Nghệ Sử Dụng

* **Ngôn ngữ**: Kotlin (100% Modern Native Android)
* **UI Framework**: Jetpack Compose & Material 3
* **Kiến trúc**: MVVM + Repository Pattern + Clean Architecture
* **Cơ sở dữ liệu**: Room Database (KSP) + Kotlin Coroutines & StateFlow
* **Navigation**: Jetpack Navigation Compose
* **Android Service**: Telecom `CallScreeningService` API & `RoleManager` API
* **Minimum SDK**: API 26 (Android 8.0 Oreo)
* **Target SDK**: API 35 / 37 (Android 15+)

---

## 📁 Cấu Trúc Thư Mục Source Code

```
com.example.spamcallblocker_20235030/
├── data/                       # Tầng dữ liệu & Room DB
│   ├── dao/                    # Data Access Objects (BrandDao, PhoneNumberDao, BlockedCallLogDao)
│   ├── entity/                 # Room Entities (BrandEntity, PhoneNumberEntity, BlockedCallLogEntity)
│   ├── relation/               # Room Relations (BrandWithNumbers, ActivePhoneMapping)
│   ├── AppDatabase.kt          # Room Database config & Initial seed data callback
│   └── SpamBlockerRepository.kt # Repository điều phối dữ liệu
├── service/
│   └── SpamCallScreeningService.kt # Service sàng lọc & chặn cuộc gọi Android
├── ui/                         # Tầng giao diện Jetpack Compose
│   ├── brand/                  # Màn hình & ViewModel quản lý thương hiệu
│   ├── components/             # Reusable UI components (EmptyState, ConfirmDialog...)
│   ├── dashboard/              # Màn hình & ViewModel Trang chủ/Tổng quan
│   ├── history/                # Màn hình & ViewModel Lịch sử chặn
│   ├── navigation/             # App Navigation & ViewModel Factory
│   ├── settings/               # Màn hình & ViewModel Cài đặt & Kiểm tra số
│   └── theme/                  # M3 Theme, Color, Type
├── util/
│   ├── PhoneNumberUtils.kt     # Tiện ích chuẩn hóa & định dạng số điện thoại
│   └── RoleHelper.kt           # Helper kiểm tra & xin vai trò RoleManager
├── MainActivity.kt             # Single-Activity entry point
└── SpamBlockerApp.kt           # Custom Application class
```

---

## 🚀 Hướng Dẫn Cài Đặt File APK

File APK Debug đã được build hoàn chỉnh tại đường dẫn:
```text
D:\Dev\Main\Subject\DaNenTang\DaNenTang_20_10\app\build\outputs\apk\debug\app-debug.apk
```

### Các bước cài đặt trên điện thoại Android:
1. Sao chép file `app-debug.apk` vào bộ nhớ điện thoại hoặc qua cáp USB / Zalo / Drive.
2. Mở trình quản lý file trên điện thoại và nhấn vào file `app-debug.apk`.
3. Cho phép cài đặt ứng dụng từ nguồn không xác định nếu hệ thống yêu cầu.
4. Nhấn **Cài đặt** và hoàn tất.

---

## 🧪 Các Bước Kiểm Thử Chức Năng Chặn Cuộc Gọi

### Cách 1: Sử dụng "Công cụ kiểm tra nhận diện số" trực tiếp trong app
1. Mở ứng dụng -> Chuyển sang tab **Cài đặt**.
2. Tại mục **Công Cụ Kiểm Tra Nhận Diện Số**, nhập một số điện thoại (VD: `0901234567` hoặc `0938111222`).
3. Nhấn nút **Kiểm tra**.
4. Ứng dụng sẽ tự động chuẩn hóa và báo kết quả **BỊ CHẶN 🚫** kèm tên Thương hiệu tương ứng.

### Cách 2: Kiểm thử cuộc gọi thực tế từ máy khác
1. Mở ứng dụng -> Nhấn nút **Cấp Quyền Ngay** (hoặc vào tab **Cài đặt** -> **Cấp Vai Trò Sàng Lọc Cuộc Gọi**).
2. Khi hộp thoại hệ thống Android hiện ra, chọn **Chặn Cuộc Gọi Spam** làm ứng dụng Sàng lọc cuộc gọi mặc định (Default Call Screening App).
3. Vào tab **Thương hiệu**, chọn hoặc thêm một thương hiệu mới (VD: "Máy Thử Nghiệm").
4. Thêm số điện thoại của máy bạn sắp dùng để gọi thử (VD: `0912345678`). Dữ liệu hỗ trợ nhập có khoảng trắng hoặc `+84` (VD: `+84 912 345 678`).
5. Dùng máy thử nghiệm gọi vào điện thoại cài ứng dụng.
6. **Kết quả**: Cuộc gọi từ máy thử nghiệm sẽ bị điện thoại từ chối tự động. Mở tab **Lịch sử** hoặc **Trang chủ** sẽ thấy dòng nhật ký cuộc gọi bị chặn từ số điện thoại đó.

---

## ⚠️ Giới Hạn Còn Lại & Điểm Cần Lưu Ý
* **Chính sách hệ thống Android**: Android chỉ cho phép một ứng dụng nắm giữ vai trò `CallScreeningService` tại một thời điểm. Người dùng cần cấp quyền vai trò trong cài đặt Android để tính năng chặn thời gian thực hoạt động.
* **Nhận diện theo dữ liệu đối chiếu**: Ứng dụng chỉ chặn các số điện thoại đã được người dùng gán cho thương hiệu có trong cơ sở dữ liệu. Không tuyên bố tự nhận diện số lạ nếu chưa có dữ liệu đối chiếu.
