package vn.iotstar.baitap09_12.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {

    @NotBlank(message = "Username hoặc email không được để trống")
    private String login;   // Có thể là username hoặc email

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}