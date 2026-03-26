package org.example.poc.repository;

import org.example.poc.entity.SalaryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface SalaryJobRepository extends JpaRepository<SalaryJob, Long> {
    Optional<SalaryJob> findByJobKey(String jobKey);

    @Modifying
    @Query("delete from SalaryJob s where s.createdAt < :cutoff")
    int deleteByCreatedAtBefore(@Param("cutoff") Instant cutoff);
}
