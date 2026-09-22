package vn.iotstar.baitap09_12.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.baitap09_12.dto.LoginDTO;
import vn.iotstar.baitap09_12.dto.RegisterDTO;
import vn.iotstar.baitap09_12.service.AuthService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ===================== LOGIN =====================
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "auth/login";
    }

    // POST /login do Spring Security tự xử lý, không cần viết ở đây

    // ===================== REGISTER =====================
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerDTO") RegisterDTO dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.registerDTO", "Mật khẩu xác nhận không khớp");
            return "auth/register";
        }
        try {
            authService.register(dto);
            redirectAttributes.addFlashAttribute("email", dto.getEmail());
            return "redirect:/verify-otp";
        } catch (RuntimeException e) {
            result.rejectValue("email", "error.registerDTO", e.getMessage());
            return "auth/register";
        }
    }

    // ===================== VERIFY OTP =====================
    @GetMapping("/verify-otp")
    public String verifyOtpPage(@ModelAttribute("email") String email, Model model) {
        if (email == null || email.isEmpty()) {
            return "redirect:/register";
        }
        model.addAttribute("email", email);
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (authService.verifyOtp(email, otp)) {
            redirectAttributes.addFlashAttribute("verified", true);
            return "redirect:/login";
        }
        model.addAttribute("email", email);
        model.addAttribute("error", "OTP không đúng hoặc đã hết hạn");
        return "auth/verify-otp";
    }

    @PostMapping("/register/resend-otp")
    public String resendOtp(@RequestParam String email,
                            RedirectAttributes redirectAttributes) {
        try {
            authService.resendOtp(email);
            redirectAttributes.addFlashAttribute("message", "Đã gửi lại OTP");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/verify-otp";
    }

    // ===================== FORGOT PASSWORD =====================
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email,
                                 RedirectAttributes redirectAttributes) {
        try {
            authService.sendForgotPasswordOtp(email);
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/reset-password";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forgot-password";
        }
    }

    // ===================== RESET PASSWORD =====================
    @GetMapping("/reset-password")
    public String resetPasswordPage(@ModelAttribute("email") String email, Model model) {
        if (email == null || email.isEmpty()) {
            return "redirect:/forgot-password";
        }
        model.addAttribute("email", email);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
                                @RequestParam String otp,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("email", email);
            model.addAttribute("error", "Mật khẩu xác nhận không khớp");
            return "auth/reset-password";
        }
        try {
            authService.resetPassword(email, otp, newPassword);
            redirectAttributes.addFlashAttribute("reset", true);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("email", email);
            model.addAttribute("error", e.getMessage());
            return "auth/reset-password";
        }
    }

    // ===================== ACCESS DENIED =====================
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}