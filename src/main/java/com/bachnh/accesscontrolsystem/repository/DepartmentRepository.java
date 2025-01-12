package com.bachnh.accesscontrolsystem.repository;

import com.bachnh.accesscontrolsystem.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, String> {
    Department findByDepartmentCode(String departmentCode);
    Department deleteByDepartmentCode(String departmentCode);
    Department findByDepartmentName(String departmentName);
}
