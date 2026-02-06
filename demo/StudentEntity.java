package com.example.demo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name="studentfb")
public class StudentEntity {
	    @Id
	    @GeneratedValue(strategy=GenerationType.IDENTITY)
	    private int id;

	    @NotBlank(message="Enter a valid name")
	    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only alphabets")
	    private String name;

	    @Email(message="Enter valid email")
	    private String email;
	    
	    @NotBlank(message="Enter branch")
	    @Pattern(regexp = "^[A-Za-z ]+$", message = "Branch must contain only alphabets")
	    private String branch;
	    
	    @Min(value = 18, message = "Enter valid age from 18 to 25")
	    @Max(value = 25, message = "Enter valid age from 18 to 25")
		private int age;

	    @NotBlank(message="Enter correct password")
	    private String password;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getBranch() {
			return branch;
		}

		public void setBranch(String branch) {
			this.branch = branch;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	    

		
	    
	}


