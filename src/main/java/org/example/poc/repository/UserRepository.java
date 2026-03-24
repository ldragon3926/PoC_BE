package org.example.poc.repository;

import org.example.poc.entity.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    @Query("""
            select distinct u
            from Users u
            left join fetch u.employee e
            left join fetch e.department
            left join fetch u.roles r
            left join fetch r.permissions
            """)
    List<Users> findAllWithRoles();

    @Query("""
            select distinct u
            from Users u
            left join fetch u.employee e
            left join fetch e.department
            left join fetch u.roles r
            left join fetch r.permissions
            where u.id = :id
            """)
    Optional<Users> findByIdWithRoles(@Param("id") Integer id);

    @Query("""
            select distinct u
            from Users u
            left join fetch u.employee e
            left join fetch e.department
            left join fetch u.roles r
            left join fetch r.permissions
            where u.username = :username
            """)
    Optional<Users> findByUsernameWithRoles(@Param("username") String username);

    Users findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmployee_Id(Integer employeeId);

    @Query("""
            select count(u) > 0
            from Users u
            where lower(u.username) = lower(:username)
              and u.id <> :userId
            """)
    boolean existsUsernameConflictForUpdate(@Param("username") String username,
                                            @Param("userId") Integer userId);

    @Query("""
            select count(u) > 0
            from Users u
            where lower(u.email) = lower(:email)
              and u.id <> :userId
            """)
    boolean existsEmailConflictForUpdate(@Param("email") String email,
                                         @Param("userId") Integer userId);

    @Query("""
            select count(u) > 0
            from Users u
            where u.employee.id = :employeeId
              and u.id <> :userId
            """)
    boolean existsEmployeeConflictForUpdate(@Param("employeeId") Integer employeeId,
                                            @Param("userId") Integer userId);
}
