package org.example.poc.repository;

import org.example.poc.entity.Reward;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Integer> {
    @Query("""
            select r
            from Reward r
            left join fetch r.employee e
            left join fetch e.department
            """)
    List<Reward> findAllWithEmployee();

    @Query("""
            select r
            from Reward r
            left join fetch r.employee e
            left join fetch e.department
            where r.id = :id
            """)
    Optional<Reward> findByIdWithEmployee(@Param("id") Integer id);

    @Query("""
            select coalesce(sum(r.amount), 0)
            from Reward r
            where r.employee.id = :employeeId
              and year(r.createdAt) = :year
              and month(r.createdAt) = :month
            """)
    BigDecimal sumRewardByEmployeeInMonth(@Param("employeeId") Integer employeeId,
                                          @Param("month") Integer month,
                                          @Param("year") Integer year);
}
