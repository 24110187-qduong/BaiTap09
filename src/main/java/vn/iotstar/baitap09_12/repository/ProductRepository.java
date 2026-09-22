package vn.iotstar.baitap09_12.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iotstar.baitap09_12.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ===================== TÌM KIẾM + PHÂN TRANG =====================

    // Tìm theo tên sản phẩm
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Tìm theo tên HOẶC mô tả
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable);

    // Tìm sản phẩm của 1 user
    Page<Product> findByUserId(Long userId, Pageable pageable);

    // Tìm sản phẩm của 1 user theo tên
    Page<Product> findByUserIdAndNameContainingIgnoreCase(
            Long userId, String name, Pageable pageable);

    // ===================== JOIN FETCH USER =====================

    // Load product kèm user (tránh LazyInitializationException)
    @Query("SELECT p FROM Product p JOIN FETCH p.user")
    Page<Product> findAllWithUser(Pageable pageable);

    // Load product của user kèm thông tin user
    @Query("SELECT p FROM Product p JOIN FETCH p.user WHERE p.user.id = :userId")
    Page<Product> findByUserIdWithUser(@Param("userId") Long userId, Pageable pageable);

    // Tìm kiếm theo tên + join user
    @Query("SELECT p FROM Product p JOIN FETCH p.user WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchWithUser(@Param("keyword") String keyword, Pageable pageable);

    // Tìm kiếm theo tên + user + join user
    @Query("""
        SELECT p FROM Product p JOIN FETCH p.user
        WHERE p.user.id = :userId
        AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Product> searchByUserWithUser(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // ===================== THỐNG KÊ =====================

    // Đếm số sản phẩm của 1 user
    long countByUserId(Long userId);

    // Đếm tất cả sản phẩm
    long count();

    // Lấy top sản phẩm theo giá
    @Query("SELECT p FROM Product p ORDER BY p.price DESC")
    List<Product> findTopExpensive(Pageable pageable);
}