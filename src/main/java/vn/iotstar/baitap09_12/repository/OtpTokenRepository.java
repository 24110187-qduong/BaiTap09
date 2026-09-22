package vn.iotstar.baitap09_12.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.baitap09_12.entity.OtpToken;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    // Tìm OTP theo mã + user
    Optional<OtpToken> findByOtpAndUser(String otp, vn.iotstar.baitap09_12.entity.User user);

    // Tìm OTP theo email + mã
    @Query("""
        SELECT o FROM OtpToken o
        JOIN FETCH o.user u
        WHERE u.email = :email AND o.otp = :otp AND o.used = false
    """)
    Optional<OtpToken> findByEmailAndOtp(@Param("email") String email, @Param("otp") String otp);

    // Tìm OTP mới nhất của user (chưa dùng)
    @Query("""
        SELECT o FROM OtpToken o
        WHERE o.user.id = :userId AND o.used = false
        ORDER BY o.expiryDate DESC
    """)
    Optional<OtpToken> findLatestByUserId(@Param("userId") Long userId);

    // Xóa tất cả OTP của user (khi đổi mật khẩu thành công)
    @Modifying
    @Transactional
    @Query("DELETE FROM OtpToken o WHERE o.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // Xóa OTP hết hạn (có thể chạy định kỳ bằng @Scheduled)
    @Modifying
    @Transactional
    @Query("DELETE FROM OtpToken o WHERE o.expiryDate < :now")
    void deleteExpired(@Param("now") LocalDateTime now);

    // Đếm số OTP chưa dùng của user
    long countByUserIdAndUsedFalse(Long userId);

    // Kiểm tra user đã có OTP chưa dùng
    boolean existsByUserIdAndUsedFalse(Long userId);
}