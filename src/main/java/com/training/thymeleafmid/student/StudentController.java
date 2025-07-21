package com.training.thymeleafmid.student;

import com.training.thymeleafmid.LoginRequest;
import com.training.thymeleafmid.teacher.TeacherService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// TODO SWITCH SESSION INFORMATION TO TOKEN

@Controller
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final TeacherService teacherService;

    @Autowired
    public StudentController(StudentService studentService, TeacherService teacherService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    // TODO save also makes password invalid
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
            @ModelAttribute LoginRequest loginRequest, // Use @ModelAttribute to get the form data
            HttpSession session){
        if(studentService.validateLogin(loginRequest)){
            session.setAttribute("loggedInStudentId", loginRequest.getId());
            return "redirect:/student/view";
        }
        return "redirect:/student/login?error=true";
    }
    @GetMapping("/view")
    public String view(Model model, HttpSession session) {
        // Get the student's ID from the session.
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if (studentId == null) {
            return "redirect:/student/login"; // Kick them back to the login page.
        }

        // Fetch the student's full details using the ID from the session.
        Student student = studentService.findById(studentId);
        model.addAttribute("student", student); // Add the logged-in student's data to the model.

        return "student/view";
    }
    @PostMapping("register")
    public String saveNewStudent(@ModelAttribute Student student){
        studentService.saveNewStudent(student);
        return "redirect:/student/login";
    }
    @GetMapping("register")
    public String register(Model model){
        model.addAttribute("student", new Student());
        return "student/register";
    }
    @GetMapping("/teachers")
    public String teachers(Model model, HttpSession session) {
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if(studentId == null) return "redirect:/student/login";
        model.addAttribute("teachers", teacherService.findAll());
        return "student/teachers";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidate the session, clearing all attributes (like loggedInStudentId).
        session.invalidate();
        return "redirect:/student/login";
    }
    @PostMapping("/unassign/{teacherId}")
    public String unassign(@PathVariable("teacherId") long teacherId, HttpSession session){
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if(studentId == null) return "redirect:/student/login";
        teacherService.removeStudent(studentId, teacherId);
        return "redirect:/student/view";
    }
    @PostMapping("/assign/{teacherId}")
    public String assign(@PathVariable("teacherId") long teacherId, HttpSession session){
        Long studentId = (Long) session.getAttribute("loggedInStudentId");
        if(studentId == null) return "redirect:/student/login";
        teacherService.addStudent(studentId, teacherId);
        return "redirect:/student/view";
    }
}
