package vn.iotstar.baitap09_12.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.baitap09_12.dto.ProductDTO;
import vn.iotstar.baitap09_12.security.CustomUserDetails;
import vn.iotstar.baitap09_12.service.CloudinaryService;
import vn.iotstar.baitap09_12.service.ProductService;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CloudinaryService cloudinaryService;

    // ===================== LIST + SEARCH + PAGINATION =====================
    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Authentication authentication,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<ProductDTO> productPage;
        if (authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // ADMIN xem tất cả
            productPage = productService.search(keyword, pageable);
        } else {
            // USER chỉ xem sản phẩm của mình
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            productPage = productService.searchByUser(user.getId(), keyword, pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        return "products/list";
    }

    // ===================== CREATE =====================
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("productDTO", new ProductDTO());
        return "products/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("productDTO") ProductDTO dto,
                         BindingResult result,
                         @RequestParam("file") MultipartFile file,
                         Authentication authentication,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "products/form";
        }
        try {
            if (!file.isEmpty()) {
                String imageUrl = cloudinaryService.upload(file);
                dto.setImages(imageUrl);
            }
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            productService.create(dto, user.getId());
            redirectAttributes.addFlashAttribute("message", "Tạo sản phẩm thành công");
            return "redirect:/products";
        } catch (RuntimeException e) {
            result.rejectValue("name", "error.productDTO", e.getMessage());
            return "products/form";
        }
    }

    // ===================== EDIT =====================
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, Authentication authentication) {
        ProductDTO dto = productService.findById(id);
        checkOwnership(dto, authentication);
        model.addAttribute("productDTO", dto);
        return "products/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("productDTO") ProductDTO dto,
                         BindingResult result,
                         @RequestParam(value = "file", required = false) MultipartFile file,
                         Authentication authentication,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "products/form";
        }
        try {
            dto.setId(id);
            if (file != null && !file.isEmpty()) {
                String imageUrl = cloudinaryService.upload(file);
                dto.setImages(imageUrl);
            }
            checkOwnership(dto, authentication);
            productService.update(dto);
            redirectAttributes.addFlashAttribute("message", "Cập nhật sản phẩm thành công");
            return "redirect:/products";
        } catch (RuntimeException e) {
            result.rejectValue("name", "error.productDTO", e.getMessage());
            return "products/form";
        }
    }

    // ===================== DELETE =====================
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        try {
            ProductDTO dto = productService.findById(id);
            checkOwnership(dto, authentication);
            productService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Xóa sản phẩm thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    // ===================== HELPER =====================
    private void checkOwnership(ProductDTO dto, Authentication authentication) {
        if (authentication == null) return;
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return;

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        if (!dto.getUserId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền thao tác sản phẩm này");
        }
    }
}