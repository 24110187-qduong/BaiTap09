package vn.iotstar.baitap09_12.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.baitap09_12.entity.User;
import vn.iotstar.baitap09_12.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Spring Security gọi method này khi xác thực.
     * Tham số "login" có thể là username HOẶC email.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // Dùng JOIN FETCH để load kèm role, tránh LazyInitializationException
        User user = userRepository
                .findByUsernameOrEmailWithRole(login)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy username/email: " + login));

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getFullName(),
                user.getImages(),
                user.getRole().getName(),   // Ví dụ: ROLE_USER, ROLE_ADMIN
                user.isEnabled()
        );
    }
}