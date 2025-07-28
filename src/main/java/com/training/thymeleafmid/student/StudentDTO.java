package com.training.thymeleafmid.student;

import com.training.thymeleafmid.teacher.Teacher;
import lombok.Data;

@Data
public class StudentDTO {
    private String name;
    private String phoneNumber;
    private String password;
    private Teacher teacher;
}
