package com.bachnh.accesscontrolsystem.repository;

import com.bachnh.accesscontrolsystem.entity.Userlogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginRepository extends JpaRepository<Userlogin,String> {
    Userlogin findByUsername(String username);
}
