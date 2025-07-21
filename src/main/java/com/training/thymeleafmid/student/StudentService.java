package com.training.thymeleafmid.student;

import com.training.thymeleafmid.Exceptions.StudentNotFoundException;
import com.training.thymeleafmid.LoginRequest;
import com.training.thymeleafmid.teacher.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.encoder = passwordEncoder;
    }
    public boolean validateLogin(LoginRequest request) {
        String password = studentRepository.findPasswordById(request.getId());
        return encoder.matches(request.getPassword(), password);

    }
    public Student findById(long id){
        Optional<Student> studentOpt = studentRepository.findById(id);
        if(studentOpt.isPresent())
            return studentOpt.get();
        throw new StudentNotFoundException();
    }
    public void saveStudent(Student student) {

        if (student.getPassword() == null || student.getPassword().isEmpty()) {
            student.setPassword(student.getPassword());
        } else {
            student.setPassword(encoder.encode(student.getPassword()));
        }
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
}
