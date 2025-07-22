package com.training.thymeleafmid.student;

import com.training.thymeleafmid.Exceptions.StudentNotFoundException;
import com.training.thymeleafmid.teacher.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class StudentService implements UserDetailsService {
    private final StudentRepository studentRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.encoder = passwordEncoder;
    }
    public Student findById(long id){
        Optional<Student> studentOpt = studentRepository.findById(id);
        if(studentOpt.isPresent())
            return studentOpt.get();
        throw new StudentNotFoundException();
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
    public Teacher getTeacherByStudentId(long studentId) {
        return findById(studentId).getTeacher();
    }
    public void deleteStudent(Student student){
        studentRepository.delete(student);
    }
    public void saveNewStudent(Student student){
        student.setPassword(encoder.encode(student.getPassword()));
        studentRepository.save(student);
    }

//    public boolean validateLogin(LoginRequest request) {
//        String password = studentRepository.findPasswordById(request.getId());
//        return encoder.matches(request.getPassword(), password);
//
//    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student student = studentRepository.findById(Long.valueOf(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + username));

        return new User(
                String.valueOf(student.getId()),
                student.getPassword(),
                new ArrayList<>() // For this basic setup, we provide an empty list of roles (authorities).
        );
    }
}
