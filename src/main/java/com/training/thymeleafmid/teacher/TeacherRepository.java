package com.training.thymeleafmid.teacher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    @Query("SELECT t.password FROM Teacher t WHERE t.id = :id")
    String findPasswordById(long id);
}
