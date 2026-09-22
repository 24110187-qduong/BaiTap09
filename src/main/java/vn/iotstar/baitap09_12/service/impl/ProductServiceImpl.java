package vn.iotstar.baitap09_12.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.baitap09_12.dto.ProductDTO;
import vn.iotstar.baitap09_12.entity.Product;
import vn.iotstar.baitap09_12.entity.User;
import vn.iotstar.baitap09_12.mapper.ProductMapper;
import vn.iotstar.baitap09_12.repository.ProductRepository;
import vn.iotstar.baitap09_12.repository.UserRepository;
import vn.iotstar.baitap09_12.service.ProductService;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    // ===================== SEARCH (ADMIN) =====================
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> search(String keyword, Pageable pageable) {
        Page<Product> page;
        if (keyword == null || keyword.trim().isEmpty()) {
            page = productRepository.findAllWithUser(pageable);
        } else {
            page = productRepository.searchWithUser(keyword, pageable);
        }
        return page.map(productMapper::toDTO);
    }

    // ===================== SEARCH BY USER =====================
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchByUser(Long userId, String keyword, Pageable pageable) {
        Page<Product> page;
        if (keyword == null || keyword.trim().isEmpty()) {
            page = productRepository.findByUserIdWithUser(userId, pageable);
        } else {
            page = productRepository.searchByUserWithUser(userId, keyword, pageable);
        }
        return page.map(productMapper::toDTO);
    }

    // ===================== FIND BY ID =====================
    @Override
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm id = " + id));
        return productMapper.toDTO(product);
    }

    // ===================== CREATE =====================
    @Override
    @Transactional
    public void create(ProductDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Product product = productMapper.toEntity(dto);
        product.setUser(user);
        productRepository.save(product);
    }

    // ===================== UPDATE =====================
    @Override
    @Transactional
    public void update(ProductDTO dto) {
        Product product = productRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        productMapper.updateEntityFromDTO(dto, product);

        // Nếu ảnh mới được upload, set vào
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            product.setImages(dto.getImages());
        }

        productRepository.save(product);
    }

    // ===================== DELETE =====================
    @Override
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        productRepository.delete(product);
    }
}