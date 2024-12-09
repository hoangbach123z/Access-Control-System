package com.bachnh.accesscontrolsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @Column(name = "pk_id", nullable = false)
    private String pkId;

    @Column(name = "c_employeecode",length = 255)
    private String cEmployeecode;

    @Column(name = "c_fullname",length = 255)
    private String cFullname;

    @Column(name = "c_gender", length = 50)
    private String cGender;

    @Column(name = "c_birthday")
    private LocalDate cBirthday;

    @Column(name = "c_card_id", length = 50)
    private String cCardId;

    @Column(name = "c_mobie", length = 50)
    private String cMobie;

    @Column(name = "c_email", length = 100)
    private String cEmail;

    @Column(name = "c_address", length = 100)
    private String cAddress;

    @Column(name = "c_profile_image")
    private String cProfileImage;

    @Column(name = "c_qr_code")
    private String cQrCode;

    @Column(name = "c_department_id")
    private String cDepartmentId;

    @Column(name = "c_role_id")
    private String cRoleId;

    @Column(name = "c_status", length = 50)
    private String cStatus;

    @Column(name = "c_create_date")
    private Instant cCreateDate;

    @Column(name = "c_update_date")
    private Instant cUpdateDate;

}