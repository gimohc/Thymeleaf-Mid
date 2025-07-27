package com.training.thymeleafmid.student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.training.thymeleafmid.entities.User;
import com.training.thymeleafmid.teacher.Teacher;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="students")
public class Student extends User {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "teacher_id") // nullable= false)
    private Teacher teacher;
    private String phoneNumber;
    private double grade;

}
