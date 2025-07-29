package com.training.thymeleafmid.student;

import com.training.thymeleafmid.Exceptions.RoleNotFoundException;
import com.training.thymeleafmid.Exceptions.UserNotFoundException;
import com.training.thymeleafmid.admin.Role;
import com.training.thymeleafmid.admin.RoleRepository;
import com.training.thymeleafmid.user.User;
import com.training.thymeleafmid.user.UserRepository;
import jakarta.transaction.Transactional;
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
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public StudentService(UserRepository userRepository, RoleRepository roleRepository, StudentRepository studentRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.encoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }
    public Student findById(long id){
        Optional<Student> studentOpt = studentRepository.findById(id);
        return studentOpt.orElse(null);
    }
    @Transactional
    public void saveStudent(long userId, StudentDTO request) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userToUpdate.setName(request.getName());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            userToUpdate.setPassword(encoder.encode(request.getPassword()));
        }
        Student profile = userToUpdate.getStudentProfile();
        profile.setPhoneNumber(request.getPhoneNumber());
    }
    public void updateStudent(Student student) {
        studentRepository.save(student);
    }
    @Transactional
    public Student saveNewStudent(StudentDTO request){
        User user = new User();
        Student newStudent = new Student();

        Role role = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        user.addRole(role);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setName(request.getName());

        newStudent.setPhoneNumber(request.getPhoneNumber());

        newStudent.setUser(user);
        user.setStudentProfile(newStudent);

        return studentRepository.save(newStudent);
    }
    public Student authenticateStudent(Authentication authentication) {
        if(authentication == null) return null;

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        long studentId = Long.parseLong(userDetails.getUsername());
        return findById(studentId);
    }

}
