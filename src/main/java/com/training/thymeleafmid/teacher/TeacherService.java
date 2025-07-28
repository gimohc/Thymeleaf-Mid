package com.training.thymeleafmid.teacher;

import com.training.thymeleafmid.Exceptions.RoleNotFoundException;
import com.training.thymeleafmid.Exceptions.UserNotFoundException;
import com.training.thymeleafmid.admin.Role;
import com.training.thymeleafmid.admin.RoleRepository;
import com.training.thymeleafmid.student.Student;
import com.training.thymeleafmid.student.StudentService;
import com.training.thymeleafmid.user.User;
import com.training.thymeleafmid.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder encoder;
    private final StudentService studentService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, StudentService studentService
                          , PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository) {
        this.teacherRepository = teacherRepository;
        this.encoder = passwordEncoder;
        this.studentService = studentService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }
    public Teacher findById(long id){
        Optional<Teacher> teacherOpt = teacherRepository.findById(id);
        return teacherOpt.orElse(null);
    }
    @Transactional
    public void addStudent(long studentId, long teacherId) {
        Student student = studentService.findById(studentId);
        Teacher teacher = findById(teacherId);
        teacher.addStudent(student);
        student.setTeacher(teacher);
        teacherRepository.save(teacher);
        studentService.updateStudent(student);
    }
    @Transactional
    public void removeStudent(long studentId, long teacherId) {
        Student student = studentService.findById(studentId);
        Teacher teacher = findById(teacherId);
        teacher.removeStudent(student);
        student.setTeacher(null);
        teacherRepository.save(teacher);
        studentService.updateStudent(student);
    }
    public Iterable<Teacher> findAll() {
        return teacherRepository.findAll();
    }
    @Transactional
    public Teacher saveNewTeacher(TeacherDTO request) {
        User user = new User();
        Teacher teacher = new Teacher();
        Role role = roleRepository.findByName("ROLE_TEACHER")
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        user.addRole(role);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setName(request.getName());


        teacher.setPhoneNumber(request.getPhoneNumber());
        teacher.setHourlyPay(request.getHourlyPay());
        teacher.setWorkTime(request.getWorkTime());
        teacher.setUser(user);
        user.setTeacherProfile(teacher);

        return teacherRepository.save(teacher);
    }
    @Transactional
    public void updateTeacher(long userId, TeacherDTO request) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userToUpdate.setName(request.getName());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            userToUpdate.setPassword(encoder.encode(request.getPassword()));
        }
        Teacher profile = userToUpdate.getTeacherProfile();
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setHourlyPay(request.getHourlyPay());
        profile.setWorkTime(request.getWorkTime());

    }
    public Teacher authenticate(Authentication authentication) {
        if(authentication == null) return null;
        UserDetails details = (UserDetails) authentication.getPrincipal();
        long teacherId = Long.parseLong(details.getUsername());
        return findById(teacherId);
    }
}