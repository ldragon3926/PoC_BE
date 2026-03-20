package org.example.poc.repository;

import org.example.poc.entity.Salary;
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
}
