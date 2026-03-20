package org.example.poc.repository;

import org.example.poc.entity.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    @Query("""
            select distinct u
            from Users u
            left join fetch u.roles r
            left join fetch r.permissions
            where u.username = :username
            """)
    Optional<Users> findByUsernameWithRoles(@Param("username") String username);

    Users findByUsername(String username);
}
