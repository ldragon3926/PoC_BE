package org.example.poc.repository;

import org.example.poc.entity.Contract;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {
    @Query("""
            select c
            from Contract c
            left join fetch c.employee e
            left join fetch e.department
            """)
    List<Contract> findAllWithEmployee();

    @Query("""
            select c
            from Contract c
            left join fetch c.employee e
            left join fetch e.department
            where c.id = :id
            """)
    Optional<Contract> findByIdWithEmployee(@Param("id") Integer id);

    @Query("""
            select c
            from Contract c
            where c.employee.id = :employeeId
            order by coalesce(c.endDate, c.startDate) desc, c.id desc
            """)
    List<Contract> findLatestContractByEmployeeId(@Param("employeeId") Integer employeeId, Pageable pageable);
}
