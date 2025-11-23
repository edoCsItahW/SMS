package com.sms.service;

import com.sms.entity.Course;
import com.sms.entity.Student;
import com.sms.entity.Teacher;
import com.sms.repository.CourseRepository;
import com.sms.repository.StudentRepository;
import com.sms.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    // 使用 JOIN FETCH 避免懒加载问题
    public List<Course> findAll() {
        return courseRepository.findAllWithTeacher();
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findByIdWithTeacher(id);
    }

    public Optional<Course> findByCode(String code) {
        return courseRepository.findByCode(code);
    }

    public List<Course> findByNameContaining(String name) {
        return courseRepository.findByNameContaining(name);
    }

    public List<Course> findByNameOrCodeContaining(String keyword) {
        return courseRepository.findByNameOrCodeContaining(keyword);
    }

    public List<Course> findByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherIdWithTeacher(teacherId);
    }

    public List<Course> findByStudentId(Long studentId) {
        return courseRepository.findByStudentIdWithTeacher(studentId);
    }

    public List<Course> findCoursesWithoutTeacher() {
        return courseRepository.findCoursesWithoutTeacher();
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

    @Transactional
    public Course addStudentToCourse(Long courseId, Long studentId) {
        try {
            Optional<Course> courseOpt = courseRepository.findByIdWithStudents(courseId);
            Optional<Student> studentOpt = studentRepository.findById(studentId);

            if (courseOpt.isPresent() && studentOpt.isPresent()) {
                Course course = courseOpt.get();
                Student student = studentOpt.get();

                // 检查是否已经选过该课程
                if (course.getStudents().contains(student)) {
                    throw new RuntimeException("该学生已经选修此课程");
                }

                // 检查课程容量
                if (course.getSelectedCount() >= course.getCapacity()) {
                    throw new RuntimeException("课程容量已满，无法选课");
                }

                // 双向关联
                course.getStudents().add(student);
                student.getCourses().add(course); // 确保学生端也添加关联

                course.setSelectedCount(course.getSelectedCount() + 1);

                Course savedCourse = courseRepository.save(course);
                studentRepository.save(student); // 保存学生端的关联

                System.out.println("成功添加学生 " + student.getName() + " 到课程 " + course.getName());
                System.out.println("当前课程学生数: " + savedCourse.getStudents().size());

                return savedCourse;
            }
            throw new RuntimeException("课程或学生不存在");
        } catch (Exception e) {
            System.err.println("添加学生到课程失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Course assignTeacherToCourse(Long courseId, Long teacherId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        Optional<Teacher> teacherOpt = teacherRepository.findById(teacherId);

        if (courseOpt.isPresent() && teacherOpt.isPresent()) {
            Course course = courseOpt.get();
            course.setTeacher(teacherOpt.get());
            return courseRepository.save(course);
        }

        return null;
    }

    public List<Course> findAllForSelection() {
        return courseRepository.findAllWithTeacher();
    }

    public List<Course> findByStudentIdForSelection(Long studentId) {
        return courseRepository.findByStudentIdWithTeacher(studentId);
    }

    @Transactional
    public void removeStudentFromCourse(Long courseId, Long studentId) {
        Optional<Course> courseOpt = courseRepository.findByIdWithStudents(courseId);
        Optional<Student> studentOpt = studentRepository.findById(studentId);

        if (courseOpt.isPresent() && studentOpt.isPresent()) {
            Course course = courseOpt.get();
            Student student = studentOpt.get();

            if (course.getStudents().remove(student)) {
                course.setSelectedCount(Math.max(0, course.getSelectedCount() - 1));
                courseRepository.save(course);
            }
        }
    }

    // 获取课程的学生数量
    public int getStudentCount(Long courseId) {
        Optional<Course> course = courseRepository.findByIdWithStudents(courseId);
        return course.map(c -> c.getStudents().size()).orElse(0);
    }

    public Optional<Course> findByIdWithStudents(Long id) {
        return courseRepository.findByIdWithStudents(id);
    }

}