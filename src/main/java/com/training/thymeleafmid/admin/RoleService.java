package com.training.thymeleafmid.admin;

import com.training.thymeleafmid.Exceptions.RoleNotFoundException;
import com.training.thymeleafmid.Exceptions.UserNotFoundException;
import com.training.thymeleafmid.student.Student;
import com.training.thymeleafmid.student.StudentService;
import com.training.thymeleafmid.teacher.Teacher;
import com.training.thymeleafmid.teacher.TeacherService;
import com.training.thymeleafmid.user.User;
import com.training.thymeleafmid.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public void addRole(Role role) {
        roleRepository.save(role);
    }
    public void removeRole(long id) {
        Role role = findById(id);
        roleRepository.delete(role);
    }
    public Iterable<Role> findAll() {
        return roleRepository.findAll();
    }
    public Role findById(long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public void removeRolesFromUser(long teacherId) {
        User user = userRepository.findById(teacherId).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.clearRoles();
        userRepository.save(user);
    }

    @Transactional
    public void giveRole(long roleId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Role role = findById(roleId);
        if(role == null) throw new RoleNotFoundException("Role not found");
        if(!user.getRoles().contains(role)) {
            user.addRole(role);
            if(role.getName().equals("ROLE_STUDENT") && user.getStudentProfile() == null) {
                Student student = new Student();
                user.setStudentProfile(student);
                student.setUser(user);
            } else if(role.getName().equals("ROLE_TEACHER") && user.getTeacherProfile() == null) {
                Teacher teacher = new Teacher();
                teacher.setUser(user);
                user.setTeacherProfile(teacher);
            }

        }
    }
}
