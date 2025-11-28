package com.sms.service;

import com.sms.entity.Course;
import com.sms.entity.Grade;
import com.sms.entity.Student;
import com.sms.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    public List<Grade> findAll() {
        return gradeRepository.findAll();
    }

    public Optional<Grade> findById(Long id) {
        return gradeRepository.findById(id);
    }

    public List<Grade> findByStudentId(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    public List<Grade> findByCourseId(Long courseId) {
        return gradeRepository.findByCourseId(courseId);
    }

    public List<Grade> findByStudentIdAndCourseId(Long studentId, Long courseId) {
        return gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    public List<Grade> findByStudentIdAndSemester(Long studentId, String semester) {
        return gradeRepository.findByStudentIdAndSemester(studentId, semester);
    }

    public List<Grade> findByTeacherId(Long teacherId) {
        return gradeRepository.findByTeacherId(teacherId);
    }

    public Optional<Grade> findByCourseIdAndStudentId(Long courseId, Long studentId) {
        return gradeRepository.findByCourseIdAndStudentId(courseId, studentId);
    }

    public Grade save(Grade grade) {
        return gradeRepository.save(grade);
    }

    public void deleteById(Long id) {
        gradeRepository.deleteById(id);
    }

    public Grade updateGrade(Long courseId, Long studentId, Double usualScore, Double examScore) {
        Optional<Grade> gradeOpt = findByCourseIdAndStudentId(courseId, studentId);
        if (gradeOpt.isPresent()) {
            Grade grade = gradeOpt.get();
            grade.setUsualScore(usualScore);
            grade.setExamScore(examScore);
            return gradeRepository.save(grade);
        }
        return null;
    }


}