package vn.iotstar.baitap09_12.service;

import vn.iotstar.baitap09_12.dto.RegisterDTO;

public interface AuthService {
    void register(RegisterDTO dto);
    boolean verifyOtp(String email, String otp);
    void resendOtp(String email);
    void sendForgotPasswordOtp(String email);
    void resetPassword(String email, String otp, String newPassword);
}