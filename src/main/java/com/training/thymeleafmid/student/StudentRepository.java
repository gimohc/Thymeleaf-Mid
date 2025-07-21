package com.training.thymeleafmid.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT s.password FROM Student s WHERE s.id = :id")
    String findPasswordById(long id);
}
