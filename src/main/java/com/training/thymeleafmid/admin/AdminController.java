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
        // input field for the new role to be added
        model.addAttribute("role", new Role());

        //current available roles
        model.addAttribute("roles", roleService.findAll());

        // display all users to access their roles and manipulate them
        model.addAttribute("users", userRepository.findAll());
        return "admin/index";
    }
    @PostMapping("/createRole")
    public String createRole(@ModelAttribute Role role) {
        roleService.addRole(role);
        return "redirect:/admin/index";
    }
    @PostMapping("/deleteRole/{roleId}") // completely removing a role
    public String deleteRole(@PathVariable long roleId) {
        roleService.removeRole(roleId);
        return "redirect:/admin/index";
    }
    @PostMapping("/give") // assign a role to a user
    public String give(@RequestParam("roleId") long roleId, @RequestParam("userId") long userId) {
        roleService.giveRole(roleId, userId);
        return "redirect:/admin/index";
    }
    @PostMapping("removeRoles/{userId}") // clear user roles
    public String remove(@PathVariable long userId) {
        roleService.removeRolesFromUser(userId);
        return "redirect:/admin/index";
    }

}
