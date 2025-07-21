package com.training.thymeleafmid.teacher;

import com.training.thymeleafmid.Exceptions.TeacherNotFoundException;
import com.training.thymeleafmid.LoginRequest;
import com.training.thymeleafmid.student.Student;
import com.training.thymeleafmid.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder encoder;
    private final StudentService studentService;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, StudentService studentService
                          //, StudentRepository studentRepository
                          , PasswordEncoder passwordEncoder) {
        this.teacherRepository = teacherRepository;
        this.encoder = passwordEncoder;
        //this.studentRepository = studentRepository;
        this.studentService = studentService;
    }
    public Teacher findById(long id){
        Optional<Teacher> teacherOpt = teacherRepository.findById(id);
        if(teacherOpt.isPresent())
            return teacherOpt.get();
        throw new TeacherNotFoundException();
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
    public boolean validateLogin(LoginRequest request) {
        String password = teacherRepository.findPasswordById(request.getId());
        return encoder.matches(request.getPassword(), password);
    }
    public void saveNewTeacher(Teacher teacher) {
        teacher.setPassword(encoder.encode(teacher.getPassword()));
        teacherRepository.save(teacher);
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
    public Iterable<Student> getStudents(long teacherId){
        return findById(teacherId).getStudents();
    }
}
