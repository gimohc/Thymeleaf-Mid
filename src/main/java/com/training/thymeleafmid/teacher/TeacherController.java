package com.training.thymeleafmid.teacher;

import com.training.thymeleafmid.LoginRequest;
import com.training.thymeleafmid.config.AuthService;
import com.training.thymeleafmid.student.Student;
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
            return "redirect:/teacher/login"; // Kick them back to the login page.
        }

        // Fetch the student's full details using the ID from the session.
        model.addAttribute("teacher", teacher); // Add the logged-in student's data to the model.

        return "teacher/view";
    }
    @PostMapping("/save")
    public String saveTeacher(@ModelAttribute Teacher teacher) {
        teacherService.saveTeacher(teacher);
        return "redirect:/teacher/view";
    }
    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("details", new LoginRequest());
        return "teacher/login";
    }
    @PostMapping("/login")
    public String processLogin(
            @ModelAttribute LoginRequest loginRequest,  // Use @ModelAttribute to get the form data
            HttpServletResponse response, RedirectAttributes redirectAttributes) {

        try {
            authService.authenticateAndSetCookie(response, loginRequest, false);
            // type: true -> student, false -> teacher
            return "redirect:/teacher/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid credentials. Please try again.");
            return "redirect:/teacher/login?error=true";
        }
    }

    @GetMapping("/students")
    public String students(Model model, Authentication authentication) {
        Teacher teacher = teacherService.authenticate(authentication);
        if(teacher == null) return "redirect:/teacher/login";
        model.addAttribute("students", teacher.getStudents());
        return "teacher/students";
    }
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("teacher", new Teacher());
        return "teacher/register";
    }
    @PostMapping("/register")
    public String register(
            @ModelAttribute Teacher teacher,
           HttpServletResponse response,
           RedirectAttributes redirectAttributes
    ) {
        LoginRequest request = new LoginRequest(-1, teacher.getPassword());
        Teacher newTeacher = teacherService.saveNewTeacher(teacher);
        request.setId(newTeacher.getId());
        try {
            authService.authenticateAndSetCookie(
                    response, request, false
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Unable to automatically log in, please log in manually."
            );
            return "redirect:/teacher/login";
        }
        return "redirect:/teacher/view";

    }

    @PostMapping("/unassign/{studentId}")
    public String unassign(@PathVariable("studentId") long studentId,
                           Authentication authentication){
        Teacher teacher = teacherService.authenticate(authentication);
        if(teacher == null) return "redirect:/teacher/login";
        teacherService.removeStudent(studentId, teacher.getId());
        return "redirect:/teacher/view";
    }
    @PostMapping("/assign/{studentId}")
    public String assign(@PathVariable("studentId") long studentId,
                         Authentication authentication){
        Teacher teacher = teacherService.authenticate(authentication);
        if(teacher == null) return "redirect:/teacher/login";
        teacherService.addStudent(studentId, teacher.getId());
        return "redirect:/teacher/view";
    }
}
