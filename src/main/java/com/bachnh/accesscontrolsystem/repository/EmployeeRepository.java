package com.bachnh.accesscontrolsystem.repository;

import com.bachnh.accesscontrolsystem.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
}
