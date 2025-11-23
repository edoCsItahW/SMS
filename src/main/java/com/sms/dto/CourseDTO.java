package com.sms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseDTO {
    private Long id;
    private String name;
    private String code;
    private Integer credit;
    private Integer capacity;
    private Integer selectedCount;
    private String description;
    private String semester;
    private String classTime;
    private String classLocation;
    private String teacherName;
    private LocalDateTime createdAt;

    public CourseDTO(Long id, String name, String code, Integer credit, Integer capacity,
                     Integer selectedCount, String description, String semester,
                     String classTime, String classLocation, String teacherName,
                     LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.credit = credit;
        this.capacity = capacity;
        this.selectedCount = selectedCount;
        this.description = description;
        this.semester = semester;
        this.classTime = classTime;
        this.classLocation = classLocation;
        this.teacherName = teacherName;
        this.createdAt = createdAt;
    }
}