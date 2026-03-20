package org.example.poc.repository;

import org.example.poc.entity.Reward;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
