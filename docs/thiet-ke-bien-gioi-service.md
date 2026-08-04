# THIẾT KẾ BIÊN GIỚI SERVICE

## 1. Danh sách Service

| Service | Cổng | Database | Trách nhiệm chính |
|---|---:|---|---|
| `api-gateway` | `8080` | Không có database | Điểm vào duy nhất của hệ thống; định tuyến request; xác thực sơ bộ; cấu hình CORS. |
| `auth-service` | `8081` | `auth_db` | Quản lý User, Student, đăng ký, đăng nhập, sinh và xác thực JWT. |
| `course-service` | `8082` | `course_db` | Quản lý Course, tìm kiếm, phân trang và quản lý số chỗ của khóa học. |
| `registration-service` | `8083` | `registration_db` | Quản lý Registration; gọi sang `course-service` để thực hiện nghiệp vụ đăng ký khóa học. |

---

## 2. Nguyên tắc sở hữu dữ liệu (Data Ownership)

- Mỗi service có **database riêng**.
- Không service nào được truy cập trực tiếp vào database của service khác.
- Khi cần lấy hoặc thay đổi dữ liệu thuộc service khác, service hiện tại **phải gọi REST API** đến service đang sở hữu dữ liệu đó.
- `registration-service` không có bảng `Course`.
- `registration-service` chỉ lưu `courseId` dưới dạng dữ liệu tham chiếu.
- `courseId` chỉ là một giá trị kiểu số hoặc chuỗi, **không sử dụng khóa ngoại thật** liên kết trực tiếp đến database của `course-service`.

### Ví dụ

Khi sinh viên đăng ký khóa học:

1. Client gửi yêu cầu đăng ký đến `registration-service` thông qua `api-gateway`.
2. `registration-service` gọi REST API nội bộ của `course-service` để kiểm tra khóa học và số chỗ còn lại.
3. Nếu còn chỗ, `course-service` cập nhật số chỗ.
4. `registration-service` lưu thông tin đăng ký vào `registration_db`.

---

## 3. Bảng định tuyến Gateway dự kiến

| Route | Forward tới | Ghi chú |
|---|---|---|
| `/api/auth/**` | `http://localhost:8081` | API đăng nhập là public; các API còn lại yêu cầu JWT tùy chức năng. |
| `/api/courses/**` | `http://localhost:8082` | `GET` public; `POST`, `PUT`, `DELETE` yêu cầu role `ADMIN`. |
| `/api/registrations/**` | `http://localhost:8083` | Yêu cầu JWT với role `STUDENT` hoặc `ADMIN`. |
| `/api/public/courses` | `http://localhost:8082` | Sử dụng API Key; dành cho đối tác bên ngoài. |

---

## 4. Luồng giao tiếp tổng quát

```text
Client
  |
  v
API Gateway :8080
  |-- /api/auth/** ----------> Auth Service :8081 ----------> auth_db
  |-- /api/courses/** -------> Course Service :8082 --------> course_db
  |-- /api/registrations/** -> Registration Service :8083 --> registration_db
                                      |
                                      | REST API
                                      v
                               Course Service :8082