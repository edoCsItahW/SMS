package com.sms.service;

import com.sms.entity.Teacher;
import com.sms.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService extends UserService<Teacher, TeacherRepository> {


    public Optional<Teacher> findByTeacherId(String teacherId) {
        return repository.findByTeacherId(teacherId);
    }

    public List<Teacher> findByDepartment(String department) {
        return repository.findByDepartment(department);
    }

    public List<Teacher> findByDepartmentContaining(String department) {
        return repository.findByDepartmentContaining(department);
    }

    public Optional<Teacher> findByCourseId(Long courseId) {
        return repository.findByCourseId(courseId);
    }

}