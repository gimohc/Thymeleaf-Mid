package com.training.thymeleafmid.student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.training.thymeleafmid.user.User;
import com.training.thymeleafmid.teacher.Teacher;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Table(name="students")
@NoArgsConstructor
@ToString(exclude = {"user", "teacher"})
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    private String phoneNumber;
    private double grade;

}
