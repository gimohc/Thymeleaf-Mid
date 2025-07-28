package com.training.thymeleafmid.teacher;

import lombok.Data;

@Data
public class TeacherDTO {
    private String name;
    private String phoneNumber;
    private String password;
    private int workTime;
    private double hourlyPay;
}
