package com.training.thymeleafmid.teacher;

import com.training.thymeleafmid.LoginRequest;
import com.training.thymeleafmid.config.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private final TeacherService teacherService;
    private final AuthService authService;

    @Autowired
    public TeacherController(AuthService authService, TeacherService teacherService) {
        this.teacherService = teacherService;
        this.authService = authService;
    }

    @GetMapping("/view")
    public String view(Model model, Authentication authentication) {
        Teacher teacher = teacherService.authenticate(authentication);
        if (teacher == null) {
            return "redirect:/login"; // Kick them back to the login page.
        }
        TeacherDTO dto = new TeacherDTO(teacher);
        model.addAttribute("teacher", dto); // Add the logged-in student's data to the model.

        return "teacher/view";
    }
    @PostMapping("/save")
    public String saveTeacher(@ModelAttribute TeacherDTO request, Authentication authentication) {
        Teacher teacher = teacherService.authenticate(authentication);
        teacherService.updateTeacher(teacher.getId(), request);
        return "redirect:/teacher/view";
    }
    @GetMapping("/students")
    public String students(Model model, Authentication authentication) {
        Teacher teacher = teacherService.authenticate(authentication);
        if(teacher == null) return "redirect:/login";
        model.addAttribute("students", teacher.getStudents());
        return "teacher/students";
    }
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("teacher", new TeacherDTO());
        return "teacher/register";
    }
    @PostMapping("/register")
    public String register(
            @ModelAttribute TeacherDTO teacher,
           HttpServletResponse response,
           RedirectAttributes redirectAttributes
    ) {
        LoginRequest request = new LoginRequest(-1, teacher.getPassword());
        Teacher newTeacher = teacherService.saveNewTeacher(teacher);
        request.setId(newTeacher.getId());
        try {
            authService.authenticateAndSetCookie(
                    response, request
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Unable to automatically log in, please log in manually."
            );
            return "redirect:/login";
        }
        return "redirect:/teacher/view";

    }
    @PostMapping("/unassign/{studentId}")
    public String unassign(@PathVariable("studentId") long studentId,
                           Authentication authentication){
        Teacher teacher = teacherService.authenticate(authentication);
        if(teacher == null) return "redirect:/login";
        teacherService.removeStudent(studentId, teacher.getId());
        return "redirect:/teacher/view";
    }
    @PostMapping("/assign/{studentId}")
    public String assign(@PathVariable("studentId") long studentId,
                         Authentication authentication){
        Teacher teacher = teacherService.authenticate(authentication);
        if(teacher == null) return "redirect:/login";
        teacherService.addStudent(studentId, teacher.getId());
        return "redirect:/teacher/view";
    }
}
