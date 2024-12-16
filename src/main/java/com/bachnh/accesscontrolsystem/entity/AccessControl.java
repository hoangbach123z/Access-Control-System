package com.bachnh.accesscontrolsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "accesscontrols")
public class AccessControl {
    @Id
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "pk_id", nullable = false)
    private UUID id;

    @Column(name = "c_code")
    private String cCode;

    @Column(name = "c_full_name")
    private String cFullName;

    @Column(name = "c_card_id", length = 50)
    private String cCardId;

    @Column(name = "c_department_name")
    private String cDepartmentName;


    @Column(name = "c_role_name")
    private String cRoleName;


    @Column(name = "c_type")
    private String cType;


    @Column(name = "c_status", length = 50)
    private String cStatus;

    @Column(name = "c_check_in")
    private LocalDateTime cCheckIn;

    @Column(name = "c_check_out")
    private LocalDateTime cCheckOut;

}