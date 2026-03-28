package com.unsaid.api.repository;

import com.unsaid.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByHandle(String handle);
}
