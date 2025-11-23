package com.sms.repository;

import com.sms.dto.CourseDTO;
import com.sms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCode(String code);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.teacher WHERE c.name LIKE %:name%")
    List<Course> findByNameContaining(@Param("name") String name);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.teacher WHERE c.name LIKE %:keyword% OR c.code LIKE %:keyword%")
    List<Course> findByNameOrCodeContaining(@Param("keyword") String keyword);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.teacher WHERE c.teacher.id = :teacherId")
    List<Course> findByTeacherIdWithTeacher(@Param("teacherId") Long teacherId);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.teacher WHERE c.id = :id")
    Optional<Course> findByIdWithTeacher(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.students WHERE c.id = :id")
    Optional<Course> findByIdWithStudents(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.students LEFT JOIN FETCH c.teacher WHERE c.teacher.id = :teacherId")
    List<Course> findByTeacherIdWithStudents(@Param("teacherId") Long teacherId);

    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    List<Course> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT c FROM Course c WHERE c.teacher IS NULL")
    List<Course> findCoursesWithoutTeacher();

    @Query("SELECT new com.sms.dto.CourseDTO(c.id, c.name, c.code, c.credit, c.capacity, " +
            "c.selectedCount, c.description, c.semester, c.classTime, c.classLocation, " +
            "t.name, c.createdAt) " +
            "FROM Course c LEFT JOIN c.teacher t WHERE c.teacher.id = :teacherId")
    List<CourseDTO> findCourseDTOsByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.teacher ORDER BY c.name")
    List<Course> findAllWithTeacher();

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.teacher JOIN c.students s WHERE s.id = :studentId ORDER BY c.name")
    List<Course> findByStudentIdWithTeacher(@Param("studentId") Long studentId);

}