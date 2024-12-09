package com.bachnh.accesscontrolsystem.repository;

import com.bachnh.accesscontrolsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
