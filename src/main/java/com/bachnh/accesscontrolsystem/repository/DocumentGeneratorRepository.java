package com.bachnh.accesscontrolsystem.repository;

import com.bachnh.accesscontrolsystem.entity.DocumentGenerator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentGeneratorRepository extends JpaRepository<DocumentGenerator, String> {
    DocumentGenerator findByEmployeeCodeAndTemplate(String employeeCode, String template);
}
