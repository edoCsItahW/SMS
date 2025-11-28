package com.sms.repository;

import com.sms.entity.Student;
import com.sms.enums.Gender;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends UserRepository<Student> {

    Optional<Student> findByStudentId(String studentId);

    List<Student> findByClassName(String className);

    List<Student> findByGender(Gender gender);

    @Query("SELECT s FROM Student s JOIN s.courses c WHERE c.id = :courseId")
    List<Student> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT s FROM Student s WHERE s.className LIKE %:className%")
    List<Student> findByClassNameContaining(@Param("className") String className);

    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.id = :id")
    Optional<Student> findByIdWithCourses(@Param("id") Long id);

}