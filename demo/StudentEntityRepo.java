package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentEntityRepo extends JpaRepository<StudentEntity, Integer> {
	StudentEntity findByNameAndPassword(String name, String password);
	

}
