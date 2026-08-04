# BLUEPRINT API

## 1. Auth Service

- **Cổng:** `8081`
- **Tiền tố khi đi qua Gateway:** `/api/auth`

| Method | Endpoint | Mô tả | Yêu cầu |
|---|---|---|---|
| `POST` | `/auth/login` | Đăng nhập, trả về JWT | Public |
| `POST` | `/auth/register` | Đăng ký tài khoản *(tùy chọn)* | Public |

---

## 2. Course Service

- **Cổng:** `8082`
- **Tiền tố khi đi qua Gateway:** `/api/courses`

| Method | Endpoint | Mô tả | Yêu cầu |
|---|---|---|---|
| `GET` | `/courses` | Lấy danh sách môn học, hỗ trợ tìm kiếm và phân trang | Public |
| `GET` | `/courses/{id}` | Lấy chi tiết một môn học | Public |
| `POST` | `/courses` | Thêm môn học | `ADMIN` |
| `PUT` | `/courses/{id}` | Sửa môn học | `ADMIN` |
| `DELETE` | `/courses/{id}` | Xóa môn học | `ADMIN` |

### API nội bộ

> Chỉ được gọi từ `registration-service`, không đưa ra Gateway cho Frontend.

| Method | Endpoint | Mô tả |
|---|---|---|
| `PATCH` | `/internal/courses/{id}/reserve-seat` | Kiểm tra còn chỗ và trừ `soChoConLai`, xử lý trong transaction |
| `PATCH` | `/internal/courses/{id}/release-seat` | Hoàn trả một chỗ khi hủy đăng ký |

---

## 3. Registration Service

- **Cổng:** `8083`
- **Tiền tố khi đi qua Gateway:** `/api/registrations`

| Method | Endpoint | Mô tả | Yêu cầu |
|---|---|---|---|
| `POST` | `/registrations` | Đăng ký học phần, gọi nội bộ sang `course-service` để giữ chỗ | `STUDENT` |
| `GET` | `/registrations/my` | Lấy danh sách đăng ký của người dùng hiện tại | `STUDENT` |
| `DELETE` | `/registrations/{id}` | Hủy đăng ký, gọi nội bộ API `release-seat` | `STUDENT` hoặc `ADMIN` |