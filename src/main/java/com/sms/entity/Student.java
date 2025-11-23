package com.sms.entity;

import com.sms.enums.Gender;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Table(name = "student")
@Entity
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"courses", "vacations"}) // 排除懒加载的关联字段
public class Student extends User {

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Gender gender;

    @Column(name = "student_id", length = 10, nullable = false)
    private String studentId;

    @Column(name = "class_name", length = 50, nullable = false)
    private String className;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "student_course",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id")
    )
    private Set<Course> courses;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vacation> vacations;

    // 自定义toString方法，避免访问懒加载字段
    @Override
    public String toString() {
        return "Student{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", studentId='" + studentId + '\'' +
                ", className='" + className + '\'' +
                ", gender=" + gender +
                '}';
    }
}