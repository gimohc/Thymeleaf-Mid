package com.training.thymeleafmid.teacher;


import com.training.thymeleafmid.student.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Entity
@Data
@Table(name="teachers")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String password;
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

    public void addStudent(Student student){
        students.add(student);
        student.setTeacher(this);
    }
    public void removeStudent(Student student){
        students.remove(student);
        student.setTeacher(null);
    }

}
