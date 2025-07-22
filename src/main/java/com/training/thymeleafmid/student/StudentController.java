package com.training.thymeleafmid.student;

import com.training.thymeleafmid.LoginRequest;
import com.training.thymeleafmid.config.JwtUtil;
import com.training.thymeleafmid.teacher.TeacherService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public StudentController(StudentService studentService, TeacherService teacherService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    private Authentication saveCookie(HttpServletResponse response, LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getId(), request.getPassword())
        );
        String jwt = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
        Cookie cookie = new Cookie("token", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        return authentication;
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
            , HttpServletResponse response){

        if(saveCookie(response, loginRequest).isAuthenticated())
            return "redirect:/student/view";

        return "redirect:/student/login?error=true";
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
    @PostMapping("register")
    public String saveNewStudent(@ModelAttribute Student student, HttpServletResponse response){
        studentService.saveNewStudent(student);
        saveCookie(response, new LoginRequest(student.getId(), student.getPassword()));
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

        Cookie cookie = new Cookie("token", null); // Erase the value
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Tell the browser to delete it immediately
        response.addCookie(cookie);

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
