package com.example.jwtexample.repository;

import com.example.jwtexample.model.DAOUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<DAOUser, Long> {

  DAOUser findByUsername(String username);
}
