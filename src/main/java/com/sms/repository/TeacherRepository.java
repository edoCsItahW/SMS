package com.sms.repository;

import com.sms.entity.Teacher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends UserRepository<Teacher> {

    Optional<Teacher> findByTeacherId(String teacherId);

    List<Teacher> findByDepartment(String department);

    @Query("SELECT t FROM Teacher t WHERE t.department LIKE %:department%")
    List<Teacher> findByDepartmentContaining(@Param("department") String department);

    @Query("SELECT t FROM Teacher t JOIN t.courses c WHERE c.id = :courseId")
    Optional<Teacher> findByCourseId(@Param("courseId") Long courseId);

}