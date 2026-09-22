package vn.iotstar.baitap09_12.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.baitap09_12.dto.UserDTO;
import vn.iotstar.baitap09_12.entity.Role;
import vn.iotstar.baitap09_12.entity.User;
import vn.iotstar.baitap09_12.mapper.UserMapper;
import vn.iotstar.baitap09_12.repository.RoleRepository;
import vn.iotstar.baitap09_12.repository.UserRepository;
import vn.iotstar.baitap09_12.service.UserService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // ===================== SEARCH + PAGINATION =====================
    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> search(String keyword, Pageable pageable) {
        Page<User> page;
        if (keyword == null || keyword.trim().isEmpty()) {
            page = userRepository.findAll(pageable);
        } else {
            page = userRepository
                    .findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                            keyword, keyword, pageable);
        }
        return page.map(user -> {
            UserDTO dto = userMapper.toDTO(user);
            dto.setProductCount(user.getProducts().size());
            return dto;
        });
    }

    // ===================== FIND BY ID =====================
    @Override
    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user id = " + id));
        UserDTO dto = userMapper.toDTO(user);
        dto.setProductCount(user.getProducts().size());
        return dto;
    }

    // ===================== CREATE =====================
    @Override
    @Transactional
    public void create(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }

        User user = userMapper.toEntity(dto);
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);
    }

    // ===================== UPDATE =====================
    @Override
    @Transactional
    public void update(UserDTO dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // Kiểm tra trùng email nếu đổi email
        if (!user.getEmail().equals(dto.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        userMapper.updateEntityFromDTO(dto, user);

        // Cập nhật role nếu có
        if (dto.getRoleId() != null) {
            Role role = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role không tồn tại"));
            user.setRole(role);
        }

        // Cập nhật mật khẩu nếu có
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userRepository.save(user);
    }

    // ===================== DELETE =====================
    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        // Không cho xóa chính mình? (tùy nghiệp vụ)
        // Không cho xóa ADMIN cuối cùng? (tùy nghiệp vụ)

        userRepository.delete(user);
    }

    // ===================== FIND ALL ROLES =====================
    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    // ===================== STATISTICS =====================
    @Override
    public long countAll() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> countProductsByUser() {
        Map<String, Long> result = new LinkedHashMap<>();
        userRepository.findAll().forEach(u ->
                result.put(u.getUsername(), (long) u.getProducts().size())
        );
        return result;
    }
}