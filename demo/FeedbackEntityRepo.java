package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackEntityRepo extends JpaRepository<FeedbackEntity, Integer> {
    boolean existsByStudentNameAndFacultyId(String studentName, String facultyId);
    long countByStudentName(String studentName);
    List<FeedbackEntity> findByFacultyId(String facultyId);


}
