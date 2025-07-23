package com.training.thymeleafmid.config;

import com.training.thymeleafmid.student.Student;
import com.training.thymeleafmid.student.StudentRepository;
import com.training.thymeleafmid.teacher.Teacher;
import com.training.thymeleafmid.teacher.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class UserDetailsCentralService implements UserDetailsService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public UserDetailsCentralService (StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameWithPrefix) throws UsernameNotFoundException {
        String[] parts = usernameWithPrefix.split(":");
        if (parts.length != 2) {
            throw new UsernameNotFoundException("Invalid username format");
        }
        String accountType= parts[0];
        String id = parts[1];

        switch(accountType){
            case "student": {
                long studentId = Long.parseLong(id);
                Student student = studentRepository.findById(studentId)
                        .orElseThrow(() -> new UsernameNotFoundException("Student not found with ID: " + id));

                return new User(
                        usernameWithPrefix,
                        student.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
                );
            }
            case "teacher": {
                long teacherId = Long.parseLong(id);
                Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(
                        () -> new UsernameNotFoundException("Teacher with ID not found: " + id));
                return new User(
                        usernameWithPrefix,
                        teacher.getPassword(),
                        teacher.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList()
                );
            }
            default: throw new UsernameNotFoundException("Invalid account type");
        }


    }
}
