package com.sms.repository;

import com.sms.entity.Vacation;
import com.sms.enums.VacationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VacationRepository extends JpaRepository<Vacation, Long> {

    @Query("SELECT v FROM Vacation v WHERE v.student.id = :studentId")
    List<Vacation> findByStudentId(@Param("studentId") Long studentId);

    List<Vacation> findByStatus(VacationStatus status);

    List<Vacation> findByApprovedById(Long teacherId);

    @Query("SELECT v FROM Vacation v WHERE v.student.id = :studentId AND v.status = :status")
    List<Vacation> findByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") VacationStatus status);

    @Query("SELECT v FROM Vacation v WHERE v.startDate <= :date AND v.endDate >= :date")
    List<Vacation> findActiveVacationsOnDate(@Param("date") LocalDate date);

    @Query("SELECT v FROM Vacation v WHERE v.createdAt BETWEEN :startDate AND :endDate")
    List<Vacation> findVacationsCreatedBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Vacation> findByCourseId(Long id);

    @Query("SELECT v FROM Vacation v LEFT JOIN FETCH v.student LEFT JOIN FETCH v.course LEFT JOIN FETCH v.approvedBy WHERE v.course.id = :courseId ORDER BY v.createdAt DESC")
    List<Vacation> findByCourseIdWithDetails(@Param("courseId") Long courseId);

    @Query("SELECT v FROM Vacation v LEFT JOIN FETCH v.course LEFT JOIN FETCH v.student LEFT JOIN FETCH v.approvedBy WHERE v.student.id = :studentId ORDER BY v.createdAt DESC")
    List<Vacation> findByStudentIdWithDetails(@Param("studentId") Long studentId);

}