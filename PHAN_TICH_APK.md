# Ghi chú khảo sát Glassify.apk

APK tham khảo có nhiều nhóm tài nguyên widget, gồm bố cục media, analog clock, date, photo và các biến thể cho launcher/Android khác nhau. Điều này cho thấy ứng dụng gốc tổ chức widget thành nhiều layout riêng thay vì một giao diện duy nhất.

Bản MS Glass Widgets hiện chỉ triển khai ưu tiên:
- Media Stage: bố cục nhạc lớn.
- Media Book: bố cục nhạc ngang nhỏ.
- Media Wave: thanh nhạc mảnh.
- Volume: điều khiển âm lượng đa phương tiện.

Mã Kotlin và layout trong dự án này được viết mới. Không bao gồm mã dex, khóa ký, tài nguyên độc quyền hay mã nguồn trích xuất từ Glassify.apk.

## Kiến trúc nhạc
- `MediaNotificationListener`: đăng ký Notification Listener, quan sát phiên MediaSession và cập nhật widget khi metadata/trạng thái thay đổi.
- `MediaWidgetUpdater`: dựng ba kiểu `RemoteViews` dùng chung metadata và hành động điều khiển.
- Các provider độc lập giúp launcher hiển thị ba biến thể trong danh sách widget.

## Giới hạn nền tảng
Android AppWidget dùng RemoteViews với tập thành phần giới hạn. Nền kính ở đây là màu alpha + gradient + viền; blur nền thực sự, refraction và mặt nạ album-art phức tạp cần kỹ thuật riêng và không được bảo đảm trên mọi launcher.
