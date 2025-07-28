package com.training.thymeleafmid.admin;

import com.training.thymeleafmid.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoleService roleService;
    private final UserRepository userRepository;

    @Autowired
    public AdminController(RoleService roleService, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }
    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "admin/index";
    }
    @PostMapping("/createRole")
    public String createRole(@ModelAttribute Role role) {
        roleService.addRole(role);
        return "redirect:/admin/index";
    }
    @PostMapping("/deleteRole/{roleId}")
    public String deleteRole(@PathVariable long roleId) {
        roleService.removeRole(roleId);
        return "redirect:/admin/index";
    }
    @PostMapping("/give")
    public String give(@RequestParam("roleId") long roleId, @RequestParam("userId") long userId) {
        roleService.giveRole(roleId, userId);
        return "redirect:/admin/index";
    }
    @PostMapping("removeRoles/{userId}")
    public String remove(@PathVariable long userId) {
        roleService.removeRolesFromUser(userId);
        return "redirect:/admin/index";
    }

}
