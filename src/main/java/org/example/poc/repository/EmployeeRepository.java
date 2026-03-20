package org.example.poc.repository;

import org.example.poc.entity.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    @Query("""
            select e
            from Employee e
            left join fetch e.department
            """)
    List<Employee> findAllWithDepartment();

    @Query("""
            select e
            from Employee e
            left join fetch e.department
            where e.id = :id
            """)
    Optional<Employee> findByIdWithDepartment(@Param("id") Integer id);
}
