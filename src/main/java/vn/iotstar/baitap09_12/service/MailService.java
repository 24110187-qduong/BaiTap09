package vn.iotstar.baitap09_12.service;

public interface MailService {
    void sendOtp(String to, String otp);
    void sendForgotPasswordOtp(String to, String otp);
}