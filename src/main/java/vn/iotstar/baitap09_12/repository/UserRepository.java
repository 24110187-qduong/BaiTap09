package vn.iotstar.baitap09_12.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iotstar.baitap09_12.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ===================== ĐĂNG NHẬP =====================

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // Đăng nhập bằng username HOẶC email
    Optional<User> findByUsernameOrEmail(String username, String email);

    // Load user kèm role (JOIN FETCH) — dùng cho CustomUserDetailsService
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.username = :login OR u.email = :login")
    Optional<User> findByUsernameOrEmailWithRole(@Param("login") String login);

    // ===================== KIỂM TRA TỒN TẠI =====================

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameOrEmail(String username, String email);

    // ===================== TÌM KIẾM + PHÂN TRANG =====================

    // Tìm theo username hoặc fullName (không phân biệt hoa thường)
    Page<User> findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String username, String fullName, Pageable pageable);

    // Tìm theo email
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    // ===================== THỐNG KÊ =====================

    // Đếm số user theo role
    long countByRoleName(String roleName);

    // Đếm số user đang active
    long countByEnabled(boolean enabled);

    // Lấy danh sách user kèm role (cho trang admin)
    @Query("SELECT u FROM User u JOIN FETCH u.role ORDER BY u.id DESC")
    Page<User> findAllWithRole(Pageable pageable);
}