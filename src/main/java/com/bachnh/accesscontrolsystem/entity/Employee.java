package com.bachnh.accesscontrolsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "pk_id", nullable = false)
    private UUID ID;

    @Column(name = "c_employeecode",length = 255)
    private String employeecode;

    @Column(name = "c_fullname",length = 255)
    private String fullname;

    @Column(name = "c_gender", length = 50)
    private String gender;

    @Column(name = "c_birthday")
    private LocalDate birthday;

    @Column(name = "c_card_id", length = 50)
    private String cardId;

    @Column(name = "c_mobie", length = 50)
    private String mobile;

    @Column(name = "c_email", length = 100)
    private String email;

    @Column(name = "c_address", length = 100)
    private String address;

    @Column(name = "c_profile_image")
    private String urlProfileImage;

    @Column(name = "c_qr_code")
    private String urlQrCode;

    @Column(name = "c_department_id")
    private String departmentID;

    @Column(name = "c_role_id")
    private String roleID;

    @Column(name = "c_status", length = 50)
    private String status;

    @Column(name = "c_create_date")
    private LocalDateTime createDate;

    @Column(name = "c_update_date")
    private LocalDateTime updateDate;

}