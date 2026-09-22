package vn.iotstar.baitap09_12.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.baitap09_12.dto.UserDTO;
import vn.iotstar.baitap09_12.service.UserService;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    // ===================== LIST + SEARCH + PAGINATION =====================
    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "id") String sortBy,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       Model model) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserDTO> userPage = userService.search(keyword, pageable);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");
        return "users/list";
    }

    // ===================== CREATE =====================
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        model.addAttribute("roles", userService.findAllRoles());
        return "users/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("userDTO") UserDTO dto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", userService.findAllRoles());
            return "users/form";
        }
        try {
            userService.create(dto);
            redirectAttributes.addFlashAttribute("message", "Tạo user thành công");
            return "redirect:/users";
        } catch (RuntimeException e) {
            result.rejectValue("email", "error.userDTO", e.getMessage());
            model.addAttribute("roles", userService.findAllRoles());
            return "users/form";
        }
    }

    // ===================== EDIT =====================
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("userDTO", userService.findById(id));
        model.addAttribute("roles", userService.findAllRoles());
        return "users/form";
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("userDTO") UserDTO dto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", userService.findAllRoles());
            return "users/form";
        }
        try {
            dto.setId(id);
            userService.update(dto);
            redirectAttributes.addFlashAttribute("message", "Cập nhật user thành công");
            return "redirect:/users";
        } catch (RuntimeException e) {
            result.rejectValue("email", "error.userDTO", e.getMessage());
            model.addAttribute("roles", userService.findAllRoles());
            return "users/form";
        }
    }

    // ===================== DELETE =====================
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Xóa user thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    // ===================== STATISTICS =====================
    @GetMapping("/stats")
    public String stats(Model model) {
        model.addAttribute("totalUsers", userService.countAll());
        model.addAttribute("userStats", userService.countProductsByUser());
        return "users/stats";
    }
}