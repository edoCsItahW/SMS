package com.sms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Data
@Entity
@Table(name = "teacher")
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "courses") // 排除懒加载的关联字段
public class Teacher extends User {

    @Column(name = "teacher_id", length = 10, nullable = false, unique = true)
    private String teacherId;

    @Column(length = 50)
    private String department;

    @Column(name = "office_location", length = 50)
    private String officeLocation;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private Set<Course> courses;

    // 自定义toString方法，避免访问懒加载字段
    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", teacherId='" + teacherId + '\'' +
                ", department='" + department + '\'' +
                ", officeLocation='" + officeLocation + '\'' +
                '}';
    }
}