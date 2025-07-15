package com.training.thymeleafmid.student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.training.thymeleafmid.teacher.Teacher;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "author_id", nullable = false)
    private Teacher teacher;
    private String phoneNumber;
    private double grade;
    private String password;

}
