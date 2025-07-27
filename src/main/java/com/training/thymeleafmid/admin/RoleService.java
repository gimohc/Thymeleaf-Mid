package com.training.thymeleafmid.admin;

import com.training.thymeleafmid.teacher.Teacher;
import com.training.thymeleafmid.teacher.TeacherService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final TeacherService teacherService;

    @Autowired
    public RoleService(RoleRepository roleRepository, TeacherService teacherService) {
        this.roleRepository = roleRepository;
        this.teacherService = teacherService;
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
    public void removeRolesFromTeacher(long teacherId) {
        Teacher teacher = teacherService.findById(teacherId);
        if(teacher != null) {
            teacher.clearRoles();
            teacherService.saveTeacher(teacher);
        }
    }
    @Transactional
    public void giveRole(long roleId, long teacherId) {
        Teacher teacher = teacherService.findById(teacherId);
        Role role = findById(roleId);
        if(teacher != null && role != null && !teacher.getRoles().contains(role)) {
            teacher.addRole(role);
            teacherService.saveTeacher(teacher);
        }
    }
}
