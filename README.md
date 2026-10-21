# MS Glass Widgets — bản tập trung Nhạc + Âm lượng

Ứng dụng Android mới viết bằng Kotlin, giao diện tiếng Việt, phong cách kính mờ xanh đen. Thiết kế tham khảo hình ảnh và cấu trúc tài nguyên widget công khai nhìn thấy trong APK Glassify; mã nguồn được viết mới, không trích xuất mã nguồn độc quyền từ APK gốc.

## Phạm vi bản này

### Ba widget nhạc độc lập
- **Media Stage:** widget lớn với ảnh bìa, tên bài hát, nghệ sĩ, thanh tiến trình, thời gian và nút bài trước / phát-tạm dừng / bài tiếp.
- **Media Book:** bố cục ngang nhỏ hơn, ảnh bìa cạnh thông tin và nút điều khiển.
- **Media Wave:** thanh nhạc mảnh với ảnh bìa nhỏ và các nút điều khiển.
- Tự tìm phiên MediaSession đang phát; ưu tiên phiên đang phát, sau đó phiên tạm dừng.
- Đọc ảnh bìa, tiêu đề, nghệ sĩ, thời lượng và vị trí phát từ metadata Android khi ứng dụng nhạc cung cấp.

### Widget âm lượng
- Nút tăng, giảm và tắt tiếng âm lượng đa phương tiện.
- Phần trăm và thanh mức âm lượng.

## Cách build APK bằng GitHub Actions
1. Tạo repository GitHub mới.
2. Giải nén ZIP và tải lên toàn bộ tệp/thư mục, giữ nguyên cấu trúc.
3. Mở **Actions** → **Build MS Glass Widgets APK** → **Run workflow**.
4. Khi workflow thành công, tải artifact `MS-Glass-Widgets-debug` và giải nén để lấy `app-debug.apk`.
5. Cài APK trên điện thoại.
6. Mở ứng dụng, nhấn **Cấp quyền nhận diện nhạc** và bật quyền thông báo cho MS Glass Widgets.
7. Nhấn giữ vùng trống màn hình chính → **Widget** → **MS Glass Widgets**; thêm Media Stage, Media Book, Media Wave hoặc Âm lượng.

## Giới hạn cần biết
- Phải bật quyền Notification Access để đọc phiên nhạc của ứng dụng khác. Ứng dụng nhạc phải cung cấp MediaSession hợp lệ; một số ứng dụng có thể không cung cấp đủ ảnh bìa hoặc metadata.
- Hiệu ứng kính hiện dùng nền màu trong suốt, viền sáng và bo góc. Android RemoteViews không cho phép tái tạo mọi hiệu ứng blur/refraction của Glassify; đây là bước nền để kiểm tra chức năng và tiếp tục tinh chỉnh.
- Thanh tiến trình hiển thị vị trí phát được báo về gần nhất; Android launcher không đảm bảo cập nhật mượt từng giây cho widget.
- Widget âm lượng điều khiển luồng đa phương tiện theo nấc. Android có thể giới hạn việc tắt tiếng tùy thiết bị.
- Chưa tuyên bố đã build hoặc kiểm thử trên thiết bị thật. Hãy kiểm tra trạng thái workflow trước khi cài APK.
