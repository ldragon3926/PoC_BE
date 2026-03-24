package org.example.poc.service;

import org.example.poc.entity.Attendance;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AttendanceService {
    List<Attendance> findAll();
    List<Attendance> findByEmployeeId(Integer employeeId);
    Optional<Attendance> findById(Integer id);
    Attendance getOne(Integer id);
    Attendance add(Attendance attendance);
    Attendance update(Attendance attendance, Integer id);
    void delete(Integer id);
}
