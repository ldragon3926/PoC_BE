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
    interface AttendanceWorkdaySummary {
        Integer getEmployeeId();
        Long getWorkedDays();
    }

    @Query("""
            select a
            from Attendance a
            left join fetch a.employee e
            left join fetch e.department
            where e.id = :employeeId
            """)
    List<Attendance> findAllByEmployeeIdWithEmployee(@Param("employeeId") Integer employeeId);

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

    @Query("""
            select e.id as employeeId, count(distinct a.workDate) as workedDays
            from Attendance a
            join a.employee e
            where year(a.workDate) = :year
              and month(a.workDate) = :month
              and a.workDate is not null
            group by e.id
            """)
    List<AttendanceWorkdaySummary> summarizeWorkedDaysByEmployeeInMonth(@Param("month") Integer month,
                                                                        @Param("year") Integer year);
}
