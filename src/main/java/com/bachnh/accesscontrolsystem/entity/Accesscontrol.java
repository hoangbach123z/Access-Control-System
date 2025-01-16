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
@Table(name = "accesscontrols")
public class Accesscontrol {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "pk_id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @Column(name = "c_code")
    private String code;

    @Size(max = 255)
    @Column(name = "c_full_name")
    private String fullName;

    @Size(max = 50)
    @Column(name = "c_gender", length = 50)
    private String gender;

//    @Size(max = 50)
//    @Column(name = "c_card_id", length = 50)
//    private String cardId;

    @Size(max = 255)
    @Column(name = "c_department_code")
    private String departmentCode;

    @Size(max = 255)
    @Column(name = "c_role_code")
    private String roleCode;

    @Size(max = 255)
    @Column(name = "c_type")
    private String type;

    @Size(max = 50)
    @Column(name = "c_status", length = 50)
    private String status;

    @Column(name = "c_check_in")
    private LocalDateTime checkIn;

    @Column(name = "c_check_out")
    private LocalDateTime checkOut;

    @Column(name = "c_check_date")
    private LocalDate checkDate;

}