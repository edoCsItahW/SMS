package com.sms.service;

import com.sms.entity.Vacation;
import com.sms.enums.VacationStatus;
import com.sms.repository.VacationRepository;
import com.sms.repository.StudentRepository;
import com.sms.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VacationService {

    @Autowired
    private VacationRepository vacationRepository;

    @Autowired
    private StudentRepository studentRepository; // 使用Repository而不是Service

    @Autowired
    private TeacherRepository teacherRepository; // 使用Repository而不是Service

    public List<Vacation> findAll() {
        return vacationRepository.findAll();
    }

    public Optional<Vacation> findById(Long id) {
        return vacationRepository.findById(id);
    }

    public List<Vacation> findByStatus(VacationStatus status) {
        return vacationRepository.findByStatus(status);
    }

    public List<Vacation> findByApprovedById(Long teacherId) {
        return vacationRepository.findByApprovedById(teacherId);
    }

    public List<Vacation> findByStudentIdAndStatus(Long studentId, VacationStatus status) {
        return vacationRepository.findByStudentIdAndStatus(studentId, status);
    }

    public List<Vacation> findActiveVacationsOnDate(LocalDate date) {
        return vacationRepository.findActiveVacationsOnDate(date);
    }

    public Vacation save(Vacation vacation) {
        return vacationRepository.save(vacation);
    }

    public void deleteById(Long id) {
        vacationRepository.deleteById(id);
    }

    public Vacation approveVacation(Long vacationId, Long teacherId) {
        Optional<Vacation> vacationOpt = vacationRepository.findById(vacationId);
        Optional<com.sms.entity.Teacher> teacherOpt = teacherRepository.findById(teacherId);

        if (vacationOpt.isPresent() && teacherOpt.isPresent()) {
            Vacation vacation = vacationOpt.get();
            vacation.setStatus(VacationStatus.APPROVED);
            vacation.setApprovedBy(teacherOpt.get());
            vacation.setApprovedAt(java.time.LocalDateTime.now());
            return vacationRepository.save(vacation);
        }
        return null;
    }

    public Vacation rejectVacation(Long vacationId) {
        Optional<Vacation> vacationOpt = vacationRepository.findById(vacationId);

        if (vacationOpt.isPresent()) {
            Vacation vacation = vacationOpt.get();
            vacation.setStatus(VacationStatus.REJECTED);
            return vacationRepository.save(vacation);
        }
        return null;
    }

    public List<Vacation> findByStudentId(Long studentId) {
        return vacationRepository.findByStudentIdWithDetails(studentId);
    }

    public List<Vacation> findByCourseId(Long id) {
        return vacationRepository.findByCourseIdWithDetails(id);
    }
}