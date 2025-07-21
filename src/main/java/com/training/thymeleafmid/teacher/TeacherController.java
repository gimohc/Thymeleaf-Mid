package com.training.thymeleafmid.teacher;

import com.training.thymeleafmid.LoginRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private final TeacherService teacherService;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/view")
    public String view(Model model, HttpSession session) {
        // Get the student's ID from the session.
        Long teacherId = (Long) session.getAttribute("loggedInTeacherId");
        if (teacherId == null) {
            return "redirect:/teacher/login"; // Kick them back to the login page.
        }

        // Fetch the student's full details using the ID from the session.
        Teacher teacher = teacherService.findById(teacherId);
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
            @ModelAttribute LoginRequest loginRequest, // Use @ModelAttribute to get the form data
            HttpSession session){
        if(teacherService.validateLogin(loginRequest)){
            session.setAttribute("loggedInTeacherId", loginRequest.getId());
            return "redirect:/teacher/view";
        }
        return "redirect:/teacher/login?error=true";
    }

    @GetMapping("/students")
    public String students(Model model, HttpSession session) {
        Long teacherId = (Long) session.getAttribute("loggedInTeacherId");
        if(teacherId == null) return "redirect:/teacher/login";
        model.addAttribute("students", teacherService.getStudents(teacherId));
        return "teacher/students";
    }
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("teacher", new Teacher());
        return "teacher/register";
    }
    @PostMapping("register")
    public String register(@ModelAttribute Teacher teacher){
        teacherService.saveNewTeacher(teacher);
        return "redirect:/teacher/login";

    }

    @PostMapping("/unassign/{studentId}")
    public String unassign(@PathVariable("studentId") long studentId, HttpSession session){
        Long teacherId = (Long) session.getAttribute("loggedInTeacherId");
        if(teacherId == null) return "redirect:/teacher/login";
        teacherService.removeStudent(studentId, teacherId);
        return "redirect:/teacher/view";
    }
    @PostMapping("/assign/{studentId}")
    public String assign(@PathVariable("studentId") long studentId, HttpSession session){
        Long teacherId = (Long) session.getAttribute("loggedInTeacherId");
        if(teacherId == null) return "redirect:/teacher/login";
        teacherService.addStudent(studentId, teacherId);
        return "redirect:/teacher/view";
    }
}
