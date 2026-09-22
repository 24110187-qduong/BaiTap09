package vn.iotstar.baitap09_12.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.iotstar.baitap09_12.dto.ProductDTO;

public interface ProductService {
    Page<ProductDTO> search(String keyword, Pageable pageable);
    Page<ProductDTO> searchByUser(Long userId, String keyword, Pageable pageable);
    ProductDTO findById(Long id);
    void create(ProductDTO dto, Long userId);
    void update(ProductDTO dto);
    void delete(Long id);
}