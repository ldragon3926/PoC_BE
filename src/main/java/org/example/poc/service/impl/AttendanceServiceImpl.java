package org.example.poc.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.poc.entity.Attendance;
import org.example.poc.exeption.NotFoundExeption;
import org.example.poc.repository.AttendanceRepository;
import org.example.poc.service.AttendanceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;

    @Override
    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    @Override
    public Optional<Attendance> findById(Integer id) {
        return attendanceRepository.findById(id);
    }

    @Override
    public Attendance getOne(Integer id) {
        return attendanceRepository.findById(id).get();
    }

    @Override
    @Transactional
    public Attendance add(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public Attendance update(Attendance attendance, Integer id) {
        Attendance attendanceFound = attendanceRepository.findById(id)
                .orElseThrow(() -> new NotFoundExeption("Can not find attendance with id: " + id));
        attendance.setId(attendanceFound.getId());
        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        attendanceRepository.deleteById(id);
    }
}
