package org.example.poc.repository;

import org.example.poc.entity.Attendance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    @Query("""
            select a
            from Attendance a
            left join fetch a.employee e
            left join fetch e.department
            """)
    List<Attendance> findAllWithEmployee();

    @Query("""
            select a
            from Attendance a
            left join fetch a.employee e
            left join fetch e.department
            where a.id = :id
            """)
    Optional<Attendance> findByIdWithEmployee(@Param("id") Integer id);
}
