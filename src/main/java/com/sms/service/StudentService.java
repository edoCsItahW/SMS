package com.sms.service;

import com.sms.entity.Student;
import com.sms.enums.Gender;
import com.sms.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService extends UserService<Student, StudentRepository> {

    public Optional<Student> findByStudentId(String studentId) {
        return repository.findByStudentId(studentId);
    }

    public List<Student> findByClassName(String className) {
        return repository.findByClassName(className);
    }

    public List<Student> findByGender(Gender gender) {
        return repository.findByGender(gender);
    }

    public List<Student> findByCourseId(Long courseId) {
        return repository.findByCourseId(courseId);
    }

    public List<Student> findByClassNameContaining(String className) {
        return repository.findByClassNameContaining(className);
    }

}