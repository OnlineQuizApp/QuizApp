package com.example.project_module6.repository;

import com.example.project_module6.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
@Repository
public interface IUserRepository extends JpaRepository<Users,Integer> {
    Optional<Users> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Users> findByEmail(String email);
    Users findById(int id);
    @Query("SELECT u FROM Users u WHERE " +
            "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:status IS NULL OR u.status = :status) AND " +
            "(:softDelete IS NULL OR u.softDelete = :softDelete) AND " +
            "(:createdAt IS NULL OR DATE(u.createAt) = DATE(:createdAt))")
    Page<Users> findByFilters(
            @Param("name") String name,
            @Param("role") Users.Role role,
            @Param("status") Boolean status,
            @Param("softDelete") Boolean softDelete,
            @Param("createdAt") LocalDateTime createdAt,
            Pageable pageable);
}
