package com.sms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "grade")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "usual_score")
    private Double usualScore; // 平时成绩

    @Column(name = "exam_score")
    private Double examScore; // 期末成绩

    @Column(name = "total_score")
    private Double totalScore; // 总成绩

    @Column(name = "grade_level")
    private String gradeLevel; // 成绩等级

    @Column(name = "semester")
    private String semester; // 学期

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotalScore();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotalScore();
    }

    private void calculateTotalScore() {
        if (usualScore != null && examScore != null) {
            totalScore = usualScore * 0.3 + examScore * 0.7;
            calculateGradeLevel();
        }
    }

    private void calculateGradeLevel() {
        if (totalScore >= 90) gradeLevel = "优秀";
        else if (totalScore >= 80) gradeLevel = "良好";
        else if (totalScore >= 70) gradeLevel = "中等";
        else if (totalScore >= 60) gradeLevel = "及格";
        else gradeLevel = "不及格";
    }
}