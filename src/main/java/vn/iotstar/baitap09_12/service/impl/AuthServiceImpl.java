package vn.iotstar.baitap09_12.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.baitap09_12.dto.RegisterDTO;
import vn.iotstar.baitap09_12.entity.OtpToken;
import vn.iotstar.baitap09_12.entity.Role;
import vn.iotstar.baitap09_12.entity.User;
import vn.iotstar.baitap09_12.repository.OtpTokenRepository;
import vn.iotstar.baitap09_12.repository.RoleRepository;
import vn.iotstar.baitap09_12.repository.UserRepository;
import vn.iotstar.baitap09_12.service.AuthService;
import vn.iotstar.baitap09_12.service.MailService;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    private static final int OTP_EXPIRY_MINUTES = 5;

    // ===================== REGISTER =====================
    @Override
    @Transactional
    public void register(RegisterDTO dto) {
        // 1. Kiểm tra trùng username / email
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        // 2. Kiểm tra mật khẩu khớp
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        // 3. Lấy role USER
        Role role = roleRepository.findByNameIgnoreCase("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        // 4. Tạo user (enabled = false, chờ xác thực OTP)
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .role(role)
                .enabled(false)
                .build();
        userRepository.save(user);

        // 5. Tạo OTP và gửi mail
        String otp = generateOtp();
        otpTokenRepository.save(OtpToken.builder()
                .otp(otp)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .used(false)
                .build());

        mailService.sendOtp(user.getEmail(), otp);
    }

    // ===================== VERIFY OTP =====================
    @Override
    @Transactional
    public boolean verifyOtp(String email, String otp) {
        OtpToken token = otpTokenRepository.findByEmailAndOtp(email, otp)
                .orElse(null);
        if (token == null || token.isExpired() || token.isUsed()) {
            return false;
        }

        // Kích hoạt user
        User user = token.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        // Đánh dấu OTP đã dùng
        token.setUsed(true);
        otpTokenRepository.save(token);

        return true;
    }

    // ===================== RESEND OTP =====================
    @Override
    @Transactional
    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        if (user.isEnabled()) {
            throw new RuntimeException("Tài khoản đã được kích hoạt");
        }

        // Xóa OTP cũ
        otpTokenRepository.deleteByUserId(user.getId());

        // Tạo OTP mới
        String otp = generateOtp();
        otpTokenRepository.save(OtpToken.builder()
                .otp(otp)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .used(false)
                .build());

        mailService.sendOtp(user.getEmail(), otp);
    }

    // ===================== FORGOT PASSWORD =====================
    @Override
    @Transactional
    public void sendForgotPasswordOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // Xóa OTP cũ
        otpTokenRepository.deleteByUserId(user.getId());

        String otp = generateOtp();
        otpTokenRepository.save(OtpToken.builder()
                .otp(otp)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .used(false)
                .build());

        mailService.sendForgotPasswordOtp(user.getEmail(), otp);
    }

    // ===================== RESET PASSWORD =====================
    @Override
    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        OtpToken token = otpTokenRepository.findByEmailAndOtp(email, otp)
                .orElseThrow(() -> new RuntimeException("OTP không đúng"));

        if (token.isExpired()) {
            throw new RuntimeException("OTP đã hết hạn");
        }
        if (token.isUsed()) {
            throw new RuntimeException("OTP đã được sử dụng");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        otpTokenRepository.save(token);
    }

    // ===================== HELPER =====================
    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }
}