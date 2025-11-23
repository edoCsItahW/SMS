package com.sms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "course")
@ToString(exclude = {"students", "teacher"}) // 排除懒加载的关联字段
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20, unique = true)
    private String code;

    @Column(nullable = false)
    private Integer credit = 2; // 学分，默认2分

    @Column(nullable = false)
    private Integer capacity = 50; // 课程容量

    @Column(name = "selected_count")
    private Integer selectedCount = 0; // 已选人数

    @Column(length = 200)
    private String description; // 课程描述

    @Column(name = "semester", length = 50)
    private String semester; // 学期

    @Column(name = "class_time", length = 100)
    private String classTime; // 上课时间

    @Column(name = "class_location", length = 100)
    private String classLocation; // 上课地点

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private Set<Student> students;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // 自定义toString方法，避免访问懒加载字段
    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", credit=" + credit +
                ", capacity=" + capacity +
                ", selectedCount=" + selectedCount +
                ", semester='" + semester + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}