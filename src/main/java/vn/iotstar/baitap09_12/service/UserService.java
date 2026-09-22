package vn.iotstar.baitap09_12.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.iotstar.baitap09_12.dto.UserDTO;
import vn.iotstar.baitap09_12.entity.Role;

import java.util.List;
import java.util.Map;

public interface UserService {
    Page<UserDTO> search(String keyword, Pageable pageable);
    UserDTO findById(Long id);
    void create(UserDTO dto);
    void update(UserDTO dto);
    void delete(Long id);
    List<Role> findAllRoles();
    long countAll();
    Map<String, Long> countProductsByUser();
}