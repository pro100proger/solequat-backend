package com.solequat.businesslogic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solequat.businesslogic.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);
}
