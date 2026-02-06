package com.example.demo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class FeedBackController {

    @Autowired
    private StudentEntityRepo studentRepo;

    @Autowired
    private FacultyEntityRepo facultyRepo;

    @Autowired
    private FeedbackEntityRepo feedbackRepo;

    // ================= HOME =================
    @GetMapping("/")
    public String home() {
        return "home";
    }

    // ================= ADMIN LOGIN =================
    @GetMapping("/login")
    public String adminLogin() {
        return "login";
    }
    
    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "admin_dashboard";
    }
    
 // ================= STUDENT FORM (ADMIN SIDE) =================
    @GetMapping("/student/form")
    public String showStudentForm(Model model) {
        model.addAttribute("student", new StudentEntity());
        return "student_form"; // Make sure you have student_form.html in templates
    }

    @PostMapping("/student/save")
    public String saveStudent(@ModelAttribute("student") StudentEntity student, Model model) {
        studentRepo.save(student);
        model.addAttribute("message", "Student inserted successfully!");
        model.addAttribute("student", new StudentEntity());
        return "student_form";
    }


    // ================= FACULTY FORM =================
    @GetMapping("/faculty/form")
    public String showFacultyForm(Model model) {
        model.addAttribute("faculty", new FacultyEntity());
        return "faculty_form";
    }

    @PostMapping("/faculty/save")
    public String saveFaculty(@Valid @ModelAttribute("faculty") FacultyEntity faculty,
                              BindingResult result,
                              @RequestParam("image") MultipartFile file,
                              Model model) {
        if (result.hasErrors()) {
            return "faculty_form";
        }

        try {
            File uploadDir = new File("uploads");
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get("uploads", fileName);
            Files.write(filePath, file.getBytes());

            faculty.setImagePath("/uploads/" + fileName);
            facultyRepo.save(faculty);

            model.addAttribute("message", "Faculty inserted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Error saving faculty data!");
        }

        return "faculty_form";
    }

    // ================= graph  =================
    @GetMapping("/admin/viewFeedback")
    public String viewFeedback(Model model) {
        List<FacultyEntity> faculties = facultyRepo.findAll();
        List<Map<String, Object>> chartData = new java.util.ArrayList<>();

        for (FacultyEntity f : faculties) {
            List<FeedbackEntity> feedbacks = feedbackRepo.findByFacultyId(f.getId());
            if (feedbacks.isEmpty()) continue;

            int total = 0;
            int count = 0;

            for (FeedbackEntity fb : feedbacks) {
                total += getScore(fb.getQ1());
                total += getScore(fb.getQ2());
                total += getScore(fb.getQ3());
                total += getScore(fb.getQ4());
                total += getScore(fb.getQ5());
                count += 5;
            }

            double avgPercentage = (total * 100.0) / (count * 4); // since Excellent=4, Good=3, etc.
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("name", f.getName());
            data.put("score", avgPercentage);
            chartData.add(data);
        }

        model.addAttribute("chartData", chartData);
        return "view_feedback";
    }

    private int getScore(String value) {
        if (value == null) return 0;
        switch (value.toLowerCase()) {
            case "excellent": return 4;
            case "very good": return 3;
            case "good": return 2;
            case "average": return 1;
            default: return 0;
        }
    }

    // ================= STUDENT LOGIN =================
    @GetMapping("/student/login")
    public String showStudentLogin() {
        return "student_login";
    }

    @PostMapping("/student/login")
    public String studentLogin(@RequestParam String name,
                               @RequestParam String password,
                               Model model,
                               HttpSession session) {
        StudentEntity student = studentRepo.findByNameAndPassword(name, password);
        if (student == null) {
            model.addAttribute("error", "Invalid name or password!");
            return "student_login";
        }
        
        long totalFaculties = facultyRepo.count();
        long submittedCount = feedbackRepo.countByStudentName(student.getName());

        if (submittedCount >= totalFaculties && totalFaculties > 0) {
            model.addAttribute("popupMessage", "You have already submitted your feedback!");
            session.invalidate();
            return "student_login";
        }

        session.setAttribute("student", student);
        session.setAttribute("facultyIndex", 0);
        return "redirect:/student/feedback";
    }

    // ================= SHOW FEEDBACK FORM =================
    @GetMapping("/student/feedback")
    public String showFeedbackForm(HttpSession session, Model model) {
        StudentEntity student = (StudentEntity) session.getAttribute("student");
        if (student == null)
            return "redirect:/student/login";

        Integer index = (Integer) session.getAttribute("facultyIndex");
        if (index == null) index = 0;

        List<FacultyEntity> facultyList = facultyRepo.findAll();
        if (facultyList.isEmpty()) {
            model.addAttribute("popupMessage", "No faculty found!");
            model.addAttribute("popupType", "info");
            return "feedback_form";
        }

        if (index >= facultyList.size()) {
            model.addAttribute("showThankYou", true);
            return "feedback_form";
        }

        FacultyEntity currentFaculty = facultyList.get(index);
        model.addAttribute("faculty", currentFaculty);
        model.addAttribute("isLast", index == facultyList.size() - 1);
        return "feedback_form";
    }

    // ================= SUBMIT FEEDBACK =================
    @PostMapping("/student/submitFeedback")
    public String submitFeedback(@RequestParam Map<String, String> params,
                                 HttpSession session,
                                 Model model) {

        StudentEntity student = (StudentEntity) session.getAttribute("student");
        if (student == null)
            return "redirect:/student/login";

        String facultyId = params.get("facultyId");

        boolean alreadySubmitted = feedbackRepo.existsByStudentNameAndFacultyId(student.getName(), facultyId);
        if (alreadySubmitted) {
            model.addAttribute("popupMessage", "You already submitted feedback for this faculty!");
            model.addAttribute("popupType", "error");
            return "feedback_form";
        }

        // Save feedback
        FeedbackEntity feedback = new FeedbackEntity();
        feedback.setStudentName(student.getName());
        feedback.setFacultyId(facultyId);
        feedback.setQ1(params.get("q1"));
        feedback.setQ2(params.get("q2"));
        feedback.setQ3(params.get("q3"));
        feedback.setQ4(params.get("q4"));
        feedback.setQ5(params.get("q5"));
        feedback.setComments(params.get("comments"));
        feedbackRepo.save(feedback);

        // Move to next faculty
        Integer index = (Integer) session.getAttribute("facultyIndex");
        if (index == null) index = 0;
        index++;
        session.setAttribute("facultyIndex", index);

        List<FacultyEntity> facultyList = facultyRepo.findAll();
        if (index >= facultyList.size()) {
            session.removeAttribute("facultyIndex");
            model.addAttribute("faculty", new FacultyEntity());
            model.addAttribute("popupMessage", "Thank you for your feedback!");
            model.addAttribute("popupType", "success");
            model.addAttribute("showThankYou", true);
            return "feedback_form";
        }

        return "redirect:/student/feedback";
    }
}
