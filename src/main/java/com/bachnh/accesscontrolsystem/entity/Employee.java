package com.bachnh.accesscontrolsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.validation.constraints.Size;
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

    @Size(max = 255)
    @Column(name = "c_employeecode")
    private String employeecode;

    @Size(max = 255)
    @Column(name = "c_fullname")
    private String fullname;

    @Size(max = 50)
    @Column(name = "c_gender", length = 50)
    private String gender;

    @Column(name = "c_birthday")
    private LocalDate birthday;

    @Size(max = 50)
    @Column(name = "c_card_id", length = 50)
    private String cardId;

    @Size(max = 50)
    @Column(name = "c_mobile", length = 50)
    private String mobile;

    @Size(max = 100)
    @Column(name = "c_email", length = 100)
    private String email;

    @Size(max = 100)
    @Column(name = "c_address", length = 100)
    private String address;

    @Size(max = 255)
    @Column(name = "c_profile_image")
    private String urlProfileImage;

    @Size(max = 255)
    @Column(name = "c_qr_code")
    private String urlQrCode;

    @Size(max = 255)
    @Column(name = "c_department_code")
    private String departmentCode;

    @Size(max = 255)
    @Column(name = "c_role_code")
    private String roleCode;

    @Size(max = 50)
    @Column(name = "c_status", length = 50)
    private String status;

    @Column(name = "c_create_date")
    private LocalDateTime createDate;

    @Column(name = "c_update_date")
    private LocalDateTime updateDate;

}