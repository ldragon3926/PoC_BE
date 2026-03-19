package org.example.poc.service;

import org.example.poc.entity.Attendance;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AttendanceService {
    List<Attendance> findAll();
    Attendance add(Attendance attendance);
    Attendance update(Attendance attendance, Integer id);
    void delete(Integer id);
}
