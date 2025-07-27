package com.training.thymeleafmid.admin;

import com.training.thymeleafmid.teacher.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoleService roleService;
    private final TeacherService teacherService;

    @Autowired
    public AdminController(RoleService roleService, TeacherService teacherService) {
        this.teacherService = teacherService;
        this.roleService = roleService;
    }
    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("teachers", teacherService.findAll());
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
    // not done
    @PostMapping("/give")
    public String give(@RequestParam("roleId") long roleId, @RequestParam("teacherId") long teacherId) {
        roleService.giveRole(roleId, teacherId);
        return "redirect:/admin/index";
    }
    @PostMapping("removeRoles/{teacherId}")
    public String remove(@PathVariable long teacherId) {
        roleService.removeRolesFromTeacher(teacherId);
        return "redirect:/admin/index";
    }

}
