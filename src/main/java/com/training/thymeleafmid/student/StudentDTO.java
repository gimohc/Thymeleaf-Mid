package com.training.thymeleafmid.student;

import com.training.thymeleafmid.teacher.Teacher;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentDTO {
    private String name;
    private String phoneNumber;
    private String password;
    private Teacher teacher;

    public StudentDTO(Student student) {
        this.name = student.getUser().getName();
        this.phoneNumber = student.getPhoneNumber();
        this.password = student.getUser().getPassword();
        this.teacher = student.getTeacher();
    }
}
