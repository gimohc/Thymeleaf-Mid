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

    @PostMapping("/save") // update student
    public String saveStudent(@ModelAttribute StudentDTO request, Authentication authentication) {
        Student student = studentService.authenticateStudent(authentication);
        studentService.saveStudent(student.getId(), request);
        return "redirect:/student/view";
    }
    @GetMapping("/view") // view editable personal details
    public String view(Model model, Authentication authentication) {
        Student student = studentService.authenticateStudent(authentication);
        if (student == null) {
            return "redirect:/login"; // Kick them back to the login page.
        }
        StudentDTO dto = new StudentDTO(student);
        model.addAttribute("student", dto); // Add the logged-in student's data to the model.

        return "student/view";
    }
    @PostMapping("/register")
    public String saveNewStudent(
            @ModelAttribute StudentDTO student,
            HttpServletResponse response
    ){
        // raw password needed
        LoginRequest request = new LoginRequest(-1, student.getPassword());
        // get the student's id after saving to database
        Student newStudent = studentService.saveNewStudent(student);
        request.setId(newStudent.getId());

        authService.authenticateAndSetCookie(response, request);
        // initialized student id and the raw password

        return "redirect:/student/view";
    }
    @GetMapping("register")
    public String register(Model model){
        model.addAttribute("student", new StudentDTO());
        return "student/register";
    }
    @GetMapping("/teachers")
    public String teachers(Model model, Authentication authentication) {
        Student student = studentService.authenticateStudent(authentication);
        if (student == null)
            return "redirect:/login"; // Kick them back to the login page.

        model.addAttribute("teachers", teacherService.findAll());
        return "student/teachers";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        authService.deleteCookie(response);
        return "redirect:/login";
    }

    @PostMapping("/unassign/{teacherId}")
    public String unassign(@PathVariable("teacherId") long teacherId, Authentication authentication){
        Student student = studentService.authenticateStudent(authentication);
        if(student == null) return "redirect:/login";
        teacherService.removeStudent(student.getId(), teacherId);
        return "redirect:/student/view";
    }
    @PostMapping("/assign/{teacherId}")
    public String assign(@PathVariable("teacherId") long teacherId, Authentication authentication){
        Student student = studentService.authenticateStudent(authentication);
        if(student == null) return "redirect:/login";
        teacherService.addStudent(student.getId(), teacherId);
        return "redirect:/student/view";
    }
}
