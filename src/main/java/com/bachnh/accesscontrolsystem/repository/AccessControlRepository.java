package com.bachnh.accesscontrolsystem.repository;

import com.bachnh.accesscontrolsystem.entity.Accesscontrol;
import org.springframework.aot.generate.AccessControl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessControlRepository extends JpaRepository<Accesscontrol, String> {
    Accesscontrol findByCodeAndStatus(String code, String status);
}
