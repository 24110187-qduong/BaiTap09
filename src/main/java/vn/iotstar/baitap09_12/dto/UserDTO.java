package vn.iotstar.baitap09_12.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    @NotBlank(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    // Chỉ dùng khi tạo mới hoặc đổi mật khẩu; có thể để trống khi update
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6 đến 100 ký tự")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 150, message = "Họ tên không quá 150 ký tự")
    private String fullName;

    private String images;

    @NotNull(message = "Vui lòng chọn role")
    private Long roleId;

    private String roleName;

    private boolean enabled;

    private long productCount;

    private LocalDateTime createdAt;
}