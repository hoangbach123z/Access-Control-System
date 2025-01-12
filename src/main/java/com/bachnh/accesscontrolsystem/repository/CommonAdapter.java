package com.bachnh.accesscontrolsystem.repository;

import com.bachnh.accesscontrolsystem.model.EmployeeModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CommonAdapter {
    private final static Logger log = LoggerFactory.getLogger(CommonAdapter.class.getName());
    @Autowired
    private EntityManager entityManager;
    public String generateEmployeeCode(String departmentCode) {
        String logPrefix = "Generate Employee Code";
        log.info(logPrefix + " ------ START ------");

        try {
            String queryStr = "SELECT public.generate_employee_code(:departmentCode)";
            Query query = entityManager.createNativeQuery(queryStr);
            query.setParameter("departmentCode", departmentCode);

            String response = (String) query.getSingleResult();

            log.info("{} - Generated Employee Code: {}", logPrefix, response);
            log.info(logPrefix + " ------ END ------");

            return response;

        } catch (Exception e) {
            log.error("{} - Error: {}", logPrefix, e.getMessage(), e);
            throw new RuntimeException("Failed to generate employee code", e);
        }
    }

    public List<EmployeeModel> getListEmployees() {
        final String logPrefix = "Get Employee Code";
        log.info("{} ------ START ------", logPrefix);

        try {
            // Sử dụng StringBuilder hoặc text block (Java 15+) để query dễ đọc hơn
            String queryStr = """
            SELECT * FROM public.getemployee() """;

            // Tạo native query với result set mapping
            Query query = entityManager
                    .createNativeQuery(queryStr, "EmployeeModelMapping");
            // Hoặc sử dụng @SqlResultSetMapping
            // .createNativeQuery(queryStr, EmployeeModel.class);

            // Thực hiện query và ép kiểu kết quả
            @SuppressWarnings("unchecked")
            List<EmployeeModel> data = query.getResultList();

            log.info("{} - Successfully retrieved {} employees", logPrefix, data.size());
            log.info("{} ------ END ------", logPrefix);

            return data;

        } catch (PersistenceException pe) {
            log.error("{} - Database error: {}", logPrefix, pe.getMessage(), pe);
            throw new RuntimeException("Failed to get employees from database", pe);
        } catch (Exception e) {
            log.error("{} - Unexpected error: {}", logPrefix, e.getMessage(), e);
            throw new RuntimeException("Failed to get employees", e);
        }
    }
}
