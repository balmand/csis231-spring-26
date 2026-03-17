package com.csis231.springpostgrescrud.repository;

import com.csis231.springpostgrescrud.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username,
            String email,
            Pageable pageable
    );
}
