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
@Table(name = "roles")
public class Role {
    @Id
    @Column(name = "pk_id", nullable = false)
    private UUID id;

    @Column(name = "c_role_code")
    private String roleCode;

    @Column(name = "c_role_name")
    private String roleName;

    @Column(name = "c_status", length = 50)
    private String status;

    @Column(name = "c_create_date")
    private Instant createDate;

    @Column(name = "c_update_date")
    private Instant updateDate;

}