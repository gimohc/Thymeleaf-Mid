package com.training.thymeleafmid.teacher;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeacherDTO {
    private String name;
    private String phoneNumber;
    private String password;
    private int workTime;
    private double hourlyPay;

    public TeacherDTO(Teacher teacher) {
        this.name = teacher.getUser().getName();
        this.phoneNumber = teacher.getPhoneNumber();
        this.password = teacher.getUser().getPassword();
        this.workTime = teacher.getWorkTime();
        this.hourlyPay = teacher.getHourlyPay();
    }
}
