package org.example.poc.repository;

import org.example.poc.entity.Roles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Integer> {
    @Query("""
            select distinct r
            from Roles r
            left join fetch r.permissions
            """)
    List<Roles> findAllWithPermissions();

    @Query("""
            select distinct r
            from Roles r
            left join fetch r.permissions
            where r.id = :id
            """)
    Optional<Roles> findByIdWithPermissions(@Param("id") Integer id);
}
