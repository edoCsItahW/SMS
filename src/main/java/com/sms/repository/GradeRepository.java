package com.sms.repository;

import com.sms.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByStudentId(Long studentId);

    List<Grade> findByCourseId(Long courseId);

    List<Grade> findByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.semester = :semester")
    List<Grade> findByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);

    @Query("SELECT g FROM Grade g WHERE g.course.teacher.id = :teacherId")
    List<Grade> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT g FROM Grade g WHERE g.course.id = :courseId AND g.student.id = :studentId")
    Optional<Grade> findByCourseIdAndStudentId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

}