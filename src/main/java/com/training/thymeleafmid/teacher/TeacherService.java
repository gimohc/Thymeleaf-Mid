package com.training.thymeleafmid.teacher;

import com.training.thymeleafmid.Exceptions.RoleNotFoundException;
import com.training.thymeleafmid.admin.Role;
import com.training.thymeleafmid.admin.RoleRepository;
import com.training.thymeleafmid.student.Student;
import com.training.thymeleafmid.student.StudentService;
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

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, StudentService studentService
                          , PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.teacherRepository = teacherRepository;
        this.encoder = passwordEncoder;
        this.studentService = studentService;
        this.roleRepository = roleRepository;
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
    public Teacher saveNewTeacher(Teacher teacher) {
        teacher.setPassword(encoder.encode(teacher.getPassword()));
        Role role = roleRepository.findByName("ROLE_TEACHER").orElseThrow(RoleNotFoundException::new);
        teacher.addRole(role);
        teacherRepository.save(teacher);
        return teacher;
    }
    public void saveTeacher(Teacher teacher) {
        Teacher storedTeacher = findById(teacher.getId());

        if (teacher.getPassword() == null || teacher.getPassword().isEmpty()) {
            teacher.setPassword(storedTeacher.getPassword());
        } else {
            teacher.setPassword(encoder.encode(teacher.getPassword()));
        }
        teacherRepository.save(teacher);
    }
    public Teacher authenticate(Authentication authentication) {
        if(authentication == null) return null;
        UserDetails details = (UserDetails) authentication.getPrincipal();
        long teacherId = Long.parseLong(details.getUsername().split(":")[1]);
        return findById(teacherId);
    }
}