package com.training.thymeleafmid.student;

import com.training.thymeleafmid.LoginRequest;
import com.training.thymeleafmid.config.AuthService;
import com.training.thymeleafmid.teacher.TeacherService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final AuthService authService;


    @Autowired
    public StudentController(AuthService authService, StudentService studentService, TeacherService teacherService) {
        this.studentService = studentService;
        this.authService = authService;
        this.teacherService = teacherService;
    }

    @PostMapping("/save")
    public String saveStudent(@ModelAttribute Student student) {
        studentService.saveStudent(student);
        return "redirect:/student/view";
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("details", new LoginRequest());
        return "student/login";
    }
    @PostMapping("/login")
    public String processLogin(
            @ModelAttribute LoginRequest loginRequest // Use @ModelAttribute to get the form data
            , HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ){
        try {
            authService.authenticateAndSetCookie(response, loginRequest, true);
            // type: true -> student, false -> teacher
            return "redirect:/student/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid credentials. Please try again.");
            return "redirect:/student/login?error=true";
        }
    }
    @GetMapping("/view")
    public String view(Model model, Authentication authentication) {

        Student student = studentService.authenticateStudent(authentication);
        if (student == null) {
            return "redirect:/student/login"; // Kick them back to the login page.
        }

        // Fetch the student's full details using the ID from the session.
        model.addAttribute("student", student); // Add the logged-in student's data to the model.

        return "student/view";
    }
    @PostMapping("/register")
    public String saveNewStudent(
            @ModelAttribute Student student,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ){
        LoginRequest request = new LoginRequest(-1, student.getPassword());
        Student newStudent = studentService.saveNewStudent(student);
        request.setId(newStudent.getId());
        try {
            authService.authenticateAndSetCookie(
                    response, request, true
            ); // initialized student id and the raw password
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Unable to automatically log in, please log in manually."
            );
            return "redirect:/student/login";
        }
        return "redirect:/student/view";
    }
    @GetMapping("register")
    public String register(Model model){
        model.addAttribute("student", new Student());
        return "student/register";
    }
    @GetMapping("/teachers")
    public String teachers(Model model, Authentication authentication) {
        Student student = studentService.authenticateStudent(authentication);
        if (student == null)
            return "redirect:/student/login"; // Kick them back to the login page.

        model.addAttribute("teachers", teacherService.findAll());
        return "student/teachers";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        authService.deleteCookie(response);
        return "redirect:/student/login";
    }

    @PostMapping("/unassign/{teacherId}")
    public String unassign(@PathVariable("teacherId") long teacherId, Authentication authentication){
        Student student = studentService.authenticateStudent(authentication);
        if(student == null) return "redirect:/student/login";
        teacherService.removeStudent(student.getId(), teacherId);
        return "redirect:/student/view";
    }
    @PostMapping("/assign/{teacherId}")
    public String assign(@PathVariable("teacherId") long teacherId, Authentication authentication){
        Student student = studentService.authenticateStudent(authentication);
        if(student == null) return "redirect:/student/login";
        teacherService.addStudent(student.getId(), teacherId);
        return "redirect:/student/view";
    }
}
