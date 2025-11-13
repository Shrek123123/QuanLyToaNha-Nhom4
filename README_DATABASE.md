# Hướng Dẫn Setup Database - QuanLyToaNha

## 📋 Yêu Cầu
- MySQL Server 8.0 trở lên
- Java 24
- Maven 3.6+

## 🚀 Cách 1: Sử dụng Flyway (Tự động - Khuyến nghị)

Flyway sẽ tự động tạo database và các bảng khi bạn chạy ứng dụng lần đầu.

### Bước 1: Cấu hình MySQL
Đảm bảo MySQL đang chạy với:
- Host: `localhost`
- Port: `3306`
- User: `root`
- Password: (để trống hoặc cập nhật trong `pom.xml`)

### Bước 2: Chạy Flyway Migration
```bash
mvn flyway:migrate
```

Hoặc chạy ứng dụng, Flyway sẽ tự động migrate:
```bash
mvn clean javafx:run
```

## 🛠️ Cách 2: Chạy SQL Script Thủ Công

### Bước 1: Mở MySQL Command Line hoặc MySQL Workbench

### Bước 2: Chạy script
```bash
mysql -u root -p < database_setup.sql
```

Hoặc copy nội dung file `database_setup.sql` và chạy trong MySQL Workbench.

## 📁 Cấu Trúc Database

### Các bảng chính:
- **user**: Thông tin người dùng
- **apartment**: Thông tin căn hộ
- **complaint**: Khiếu nại
- **vehicle**: Phương tiện
- **bms_system**: Hệ thống BMS
- **maintenance**: Bảo trì
- **security**: An ninh
- **cleaning**: Vệ sinh
- **customer_request**: Yêu cầu khách hàng
- **admin_task**: Nhiệm vụ hành chính

## 🔐 Tài Khoản Mặc Định

Sau khi setup, bạn có thể đăng nhập với:
- **Username**: `admin`
- **Password**: `admin123`

⚠️ **Lưu ý**: Nên đổi mật khẩu sau lần đăng nhập đầu tiên!

## ⚙️ Cấu Hình Database Connection

Nếu bạn muốn thay đổi thông tin kết nối, chỉnh sửa trong:
- `src/main/java/com/example/quanlytoanhanhom4/config/DatabaseConnection.java`
- `src/main/java/com/example/quanlytoanhanhom4/config/DatabaseInitializer.java`
- `pom.xml` (phần Flyway plugin configuration)

## 🔄 Cập Nhật Database Schema

Khi có thay đổi schema, tạo file migration mới:
```
src/main/resources/db/migration/V2__Add_new_table.sql
```

Flyway sẽ tự động chạy các migration mới khi bạn chạy ứng dụng.

## 📝 Ghi Chú

- Database name: `quanlytoanha`
- Character set: `utf8mb4`
- Collation: `utf8mb4_unicode_ci`
- Tất cả bảng sử dụng InnoDB engine

## ❓ Troubleshooting

### Lỗi kết nối database:
1. Kiểm tra MySQL đang chạy: `mysql -u root -p`
2. Kiểm tra port 3306 có bị chiếm không
3. Kiểm tra user và password trong code

### Lỗi Flyway:
1. Xóa bảng `flyway_schema_history` nếu cần reset
2. Chạy lại: `mvn flyway:clean flyway:migrate`

## 📞 Hỗ Trợ

Nếu gặp vấn đề, kiểm tra:
- Logs trong console khi chạy ứng dụng
- File `database_setup.sql` có chạy thành công không
- Flyway migration logs


