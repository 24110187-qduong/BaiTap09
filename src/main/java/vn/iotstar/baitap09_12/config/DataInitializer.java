package vn.iotstar.baitap09_12.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.baitap09_12.entity.Role;
import vn.iotstar.baitap09_12.entity.User;
import vn.iotstar.baitap09_12.repository.RoleRepository;
import vn.iotstar.baitap09_12.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // 1. Tạo role USER
            Role userRole = roleRepository.findByNameIgnoreCase("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(
                            Role.builder().name("ROLE_USER").build()
                    ));

            // 2. Tạo role ADMIN
            Role adminRole = roleRepository.findByNameIgnoreCase("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(
                            Role.builder().name("ROLE_ADMIN").build()
                    ));

            // 3. Tạo tài khoản admin mẫu
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("System Administrator")
                        .images("https://res.cloudinary.com/demo/image/upload/sample.jpg")
                        .role(adminRole)
                        .enabled(true)
                        .build();
                userRepository.save(admin);
            }

            // 4. Tạo tài khoản user mẫu
            if (userRepository.findByUsername("user01").isEmpty()) {
                User user = User.builder()
                        .username("user01")
                        .email("user01@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("Nguyễn Văn A")
                        .role(userRole)
                        .enabled(true)
                        .build();
                userRepository.save(user);
            }

            System.out.println(">>> DataInitializer: Đã tạo dữ liệu mẫu thành công!");
        };
    }
}