package com.sms.repository;

import com.sms.entity.Attendance;
import com.sms.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByCourseId(Long courseId);

    List<Attendance> findByAttendanceDate(LocalDate date);

    List<Attendance> findByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT a FROM Attendance a WHERE a.course.teacher.id = :teacherId")
    List<Attendance> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT a FROM Attendance a WHERE a.course.id = :courseId AND a.attendanceDate = :date")
    List<Attendance> findByCourseIdAndDate(@Param("courseId") Long courseId, @Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.status = :status")
    List<Attendance> findByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") AttendanceStatus status);

}