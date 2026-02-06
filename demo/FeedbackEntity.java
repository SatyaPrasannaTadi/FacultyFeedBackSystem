package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "feedback")
public class FeedbackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String studentName;
    private String facultyId;
    private String q1, q2, q3, q4, q5;
    private String comments;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getFacultyId() { return facultyId; }
    public void setFacultyId(String facultyId) { this.facultyId = facultyId; }

    public String getQ1() { return q1; }
    public void setQ1(String q1) { this.q1 = q1; }

    public String getQ2() { return q2; }
    public void setQ2(String q2) { this.q2 = q2; }

    public String getQ3() { return q3; }
    public void setQ3(String q3) { this.q3 = q3; }

    public String getQ4() { return q4; }
    public void setQ4(String q4) { this.q4 = q4; }

    public String getQ5() { return q5; }
    public void setQ5(String q5) { this.q5 = q5; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
