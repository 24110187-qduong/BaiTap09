package vn.iotstar.baitap09_12.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vn.iotstar.baitap09_12.service.MailService;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtp(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Xác thực tài khoản - Mã OTP");
            helper.setText(buildOtpEmail(otp, "xác thực tài khoản"), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi mail OTP: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendForgotPasswordOtp(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Đặt lại mật khẩu - Mã OTP");
            helper.setText(buildOtpEmail(otp, "đặt lại mật khẩu"), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi mail quên mật khẩu: " + e.getMessage(), e);
        }
    }

    // ===================== TEMPLATE HTML =====================
    private String buildOtpEmail(String otp, String purpose) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #2563eb;">Xác thực %s</h2>
                <p>Xin chào,</p>
                <p>Mã OTP của bạn là:</p>
                <h1 style="color: #dc2626; letter-spacing: 8px; text-align: center;">%s</h1>
                <p>Mã có hiệu lực trong <b>5 phút</b>. Vui lòng không chia sẻ mã này với bất kỳ ai.</p>
                <p>Nếu bạn không yêu cầu, hãy bỏ qua email này.</p>
                <hr>
                <p style="color: #64748b; font-size: 12px;">UTEShop - Hệ thống bán hàng</p>
            </div>
            """.formatted(purpose, otp);
    }
}