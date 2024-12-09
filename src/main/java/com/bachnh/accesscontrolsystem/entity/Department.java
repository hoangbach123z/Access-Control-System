package com.bachnh.accesscontrolsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "departments")
public class Department {
    @Id
    @Column(name = "pk_id", nullable = false)
    private String id;

    @Column(name = "c_department_code",length = 255)
    private String departmentCode;

    @Column(name = "c_department_name",length = 255)
    private String departmentName;

    @Column(name = "c_status", length = 50)
    private String status;

    @Column(name = "c_create_date")
    private Instant createDate;

    @Column(name = "c_update_date")
    private Instant updateDate;

}