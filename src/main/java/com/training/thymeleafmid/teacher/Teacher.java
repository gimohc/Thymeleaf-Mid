package com.training.thymeleafmid.teacher;


import com.training.thymeleafmid.admin.Role;
import com.training.thymeleafmid.entities.User;
import com.training.thymeleafmid.student.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name="teachers")
public class Teacher extends User {

    private String phoneNumber;
    private int workTime;
    private double hourlyPay;

    //mappedBy ensures that it doesn't create a conjunction table
    //cascade means that if a teacher was deleted, then so will the students
    //orphanRemoval means that if a student was deleted, it will be removed from the teacher's set
    //lazy fetch means only retrieving the data when requested

    @ToString.Exclude // Also exclude from toString
    @EqualsAndHashCode.Exclude // Exclude this field from equals and hashCode
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private Set<Student> students;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="teacher_roles", joinColumns = @JoinColumn (name= "teacher_id"), inverseJoinColumns = @JoinColumn (name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        roles.add(role);
    }
    public void removeRole(Role role) {
        roles.remove(role);
    }
    public void clearRoles() {
        roles.clear();
    }
    public void addStudent(Student student){
        students.add(student);
        student.setTeacher(this);
    }
    public void removeStudent(Student student){
        students.remove(student);
        student.setTeacher(null);
    }

}
