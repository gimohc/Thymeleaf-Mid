package com.training.thymeleafmid.user;

import com.training.thymeleafmid.admin.Role;
import com.training.thymeleafmid.student.Student;
import com.training.thymeleafmid.teacher.Teacher;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"studentProfile", "teacherProfile"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    @Column
    private String name;

    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Student studentProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Teacher teacherProfile;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )

    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void clearRoles() {
        this.roles.clear();
    }

    public void setStudentProfile(Student studentProfile) {
        if(this.studentProfile != null)
           this.studentProfile.setUser(null);
        if(studentProfile != null)
            studentProfile.setUser(this);

        this.studentProfile = studentProfile;

    }
    public void setTeacherProfile(Teacher teacherProfile) {
        if(this.teacherProfile != null)
            this.teacherProfile.setUser(null);
        if(teacherProfile != null)
            teacherProfile.setUser(this);

        this.teacherProfile = teacherProfile;
    }

}
