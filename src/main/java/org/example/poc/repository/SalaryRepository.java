package org.example.poc.repository;

import org.example.poc.entity.Salary;
import org.example.poc.entity.SalaryStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Integer> {
    @Query("""
            select s
            from Salary s
            left join fetch s.employee e
            left join fetch e.department
            """)
    List<Salary> findAllWithEmployee();

    @Query("""
            select s
            from Salary s
            left join fetch s.employee e
            left join fetch e.department
            where s.id = :id
            """)
    Optional<Salary> findByIdWithEmployee(@Param("id") Integer id);

    Optional<Salary> findByEmployee_IdAndMonthAndYear(Integer employeeId, Integer month, Integer year);

    @Query("""
            select s
            from Salary s
            where s.employee.id = :employeeId
              and (s.status is null or s.status not in :lockedStatuses)
            """)
    List<Salary> findMutableByEmployeeId(@Param("employeeId") Integer employeeId,
                                         @Param("lockedStatuses") List<SalaryStatus> lockedStatuses);

    boolean existsByEmployee_IdAndMonthAndYear(Integer employeeId, Integer month, Integer year);

    @Query("""
            select count(s) > 0
            from Salary s
            where s.employee.id = :employeeId
              and s.month = :month
              and s.year = :year
              and s.id <> :salaryId
            """)
    boolean existsConflictForUpdate(@Param("employeeId") Integer employeeId,
                                    @Param("month") Integer month,
                                    @Param("year") Integer year,
                                    @Param("salaryId") Integer salaryId);

    @Modifying
    @Query("""
            update Salary s
            set s.status = :toStatus
            where s.month = :month
              and s.year = :year
              and (s.status = :fromStatus or s.status is null)
            """)
    int bulkUpdateStatusByPeriod(@Param("month") Integer month,
                                 @Param("year") Integer year,
                                 @Param("fromStatus") SalaryStatus fromStatus,
                                 @Param("toStatus") SalaryStatus toStatus);

    long countByMonthAndYear(Integer month, Integer year);
}
