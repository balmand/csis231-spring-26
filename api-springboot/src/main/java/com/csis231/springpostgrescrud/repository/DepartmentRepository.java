package com.csis231.springpostgrescrud.repository;

import com.csis231.springpostgrescrud.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Page<Department> findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String name,
            String location,
            Pageable pageable
    );
}
