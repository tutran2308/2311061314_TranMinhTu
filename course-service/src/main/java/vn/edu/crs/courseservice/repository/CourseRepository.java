package vn.edu.crs.courseservice.repository;

import vn.edu.crs.courseservice.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByTenMonHocIgnoreCase(String tenMonHoc);

    // Buoi 3:
    // Tim ten mon hoc chua keyword, khong phan biet hoa thuong
    // va co ho tro phan trang
    Page<Course> findByTenMonHocContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );
}