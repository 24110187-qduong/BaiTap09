package vn.iotstar.baitap09_12.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.baitap09_12.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Tìm role theo tên (không phân biệt hoa thường)
    Optional<Role> findByNameIgnoreCase(String name);

    // Kiểm tra role tồn tại
    boolean existsByName(String name);
}