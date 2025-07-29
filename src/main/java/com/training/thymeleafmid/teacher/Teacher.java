package com.training.thymeleafmid.teacher;

import com.training.thymeleafmid.user.User;
import com.training.thymeleafmid.student.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.Set;

@Entity
@Data
@Table(name="teachers")
@NoArgsConstructor
@ToString(exclude = {"user", "students"})
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    private String phoneNumber;
    private int workTime;
    private double hourlyPay;

    //mappedBy ensures that it doesn't create a conjunction table
    //cascade means that if a teacher was deleted, then so will the students
    //orphanRemoval means that if a student was deleted, it will be removed from the teacher's set
    //lazy fetch means only retrieving the data when requested
    
    @EqualsAndHashCode.Exclude // Exclude this field from equals and hashCode
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private Set<Student> students;

    public Teacher(TeacherDTO dto) {
        this.user.setName(dto.getName());
        this.user.setPassword(dto.getPassword());
        this.user.setTeacherProfile(this);
        this.phoneNumber = dto.getPhoneNumber();
        this.workTime = dto.getWorkTime();
        this.hourlyPay = dto.getHourlyPay();
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
