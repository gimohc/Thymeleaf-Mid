package com.training.thymeleafmid.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public StudentService(StudentRepository studentRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.encoder = passwordEncoder;
    }
    public Student findById(long id){
        Optional<Student> studentOpt = studentRepository.findById(id);
        return studentOpt.orElse(null);
    }
    public void saveStudent(Student student) {
        Student stored = findById(student.getId());
        if (student.getPassword() == null || student.getPassword().isEmpty()) {
            student.setPassword(stored.getPassword());
        } else {
            student.setPassword(encoder.encode(student.getPassword()));
        }
        studentRepository.save(student);
    }
    public void updateStudent(Student student) {
        studentRepository.save(student);
    }
    public Student saveNewStudent(Student student){
        student.setPassword(encoder.encode(student.getPassword()));
        studentRepository.save(student);
        return student;
    }
    public Student authenticateStudent(Authentication authentication) {
        if(authentication == null) return null;

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        long studentId = Long.parseLong(userDetails.getUsername().split(":")[1]);
        return findById(studentId);
    }

}
