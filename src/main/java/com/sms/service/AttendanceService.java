package com.sms.service;

import com.sms.entity.Attendance;
import com.sms.enums.AttendanceStatus;
import com.sms.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    public Optional<Attendance> findById(Long id) {
        return attendanceRepository.findById(id);
    }

    public List<Attendance> findByStudentId(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    public List<Attendance> findByCourseId(Long courseId) {
        return attendanceRepository.findByCourseId(courseId);
    }

    public List<Attendance> findByAttendanceDate(LocalDate date) {
        return attendanceRepository.findByAttendanceDate(date);
    }

    public List<Attendance> findByStudentIdAndCourseId(Long studentId, Long courseId) {
        return attendanceRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    public List<Attendance> findByTeacherId(Long teacherId) {
        return attendanceRepository.findByTeacherId(teacherId);
    }

    public List<Attendance> findByCourseIdAndDate(Long courseId, LocalDate date) {
        return attendanceRepository.findByCourseIdAndDate(courseId, date);
    }

    public List<Attendance> findByStudentIdAndStatus(Long studentId, AttendanceStatus status) {
        return attendanceRepository.findByStudentIdAndStatus(studentId, status);
    }

    public Attendance save(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public void deleteById(Long id) {
        attendanceRepository.deleteById(id);
    }

    public Attendance recordAttendance(Long studentId, Long courseId, LocalDate date, AttendanceStatus status, String remark, Long teacherId) {
        // 检查是否已记录
        List<Attendance> existing = findByStudentIdAndCourseId(studentId, courseId);
        Optional<Attendance> todayRecord = existing.stream()
                .filter(a -> a.getAttendanceDate().equals(date))
                .findFirst();

        if (todayRecord.isPresent()) {
            // 更新现有记录
            Attendance attendance = todayRecord.get();
            attendance.setStatus(status);
            attendance.setRemark(remark);
            return attendanceRepository.save(attendance);
        } else {
            // 创建新记录
            Attendance attendance = new Attendance();
            // 这里需要设置student, course, recordedBy等关联对象
            // 实际项目中需要通过service获取这些实体
            attendance.setAttendanceDate(date);
            attendance.setStatus(status);
            attendance.setRemark(remark);
            return attendanceRepository.save(attendance);
        }
    }

}