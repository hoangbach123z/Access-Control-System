package com.bachnh.accesscontrolsystem.repository;

import com.bachnh.accesscontrolsystem.entity.Employee;
import com.bachnh.accesscontrolsystem.model.EmployeeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Employee findByEmployeecode(String employeecode);
}
