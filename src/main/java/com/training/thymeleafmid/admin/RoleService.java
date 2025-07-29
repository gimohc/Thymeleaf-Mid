package com.training.thymeleafmid.admin;

import com.training.thymeleafmid.Exceptions.RoleNotFoundException;
import com.training.thymeleafmid.Exceptions.UserNotFoundException;
import com.training.thymeleafmid.student.Student;
import com.training.thymeleafmid.teacher.Teacher;
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
    public Role findById(long id) throws RoleNotFoundException {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
    }

    public void removeRolesFromUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.clearRoles(); // only need to clear the owning side
        userRepository.save(user);
    }

    @Transactional // assign role to user
    public void giveRole(long roleId, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Role role = findById(roleId); // already throws an exception if the role does not exist

        if(!user.getRoles().contains(role)) { // check if the user already has that role
            user.addRole(role);
            if(role.getName().equals("ROLE_STUDENT") && user.getStudentProfile() == null) {
                // if a student role was given, give the user a new student profile
                user.setStudentProfile(new Student()); // bidirectional relation is configured in the setter
            } else if(role.getName().equals("ROLE_TEACHER") && user.getTeacherProfile() == null) {
                // if a teacher role was given, give the user a new teacher profile
                user.setTeacherProfile(new Teacher()); // bidirectional relation is configured in the setter
            }

        }
    }
}
