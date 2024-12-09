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
@Table(name = "registers")
public class Register {
    @Id
    @Column(name = "pk_id", nullable = false)
    private String pkId;

    @Column(name = "c_register_code")
    private String registerCode;

    @Column(name = "c_register_name")
    private String registerName;

    @Column(name = "c_card_id", length = 50)
    private String cardId;

    @Column(name = "c_mobie", length = 50)
    private String mobie;

    @Column(name = "c_email", length = 100)
    private String email;

    @Column(name = "c_address", length = 100)
    private String address;

    @Column(name = "c_status", length = 50)
    private String status;

    @Column(name = "c_create_date")
    private Instant createDate;

    @Column(name = "c_update_date")
    private Instant updateDate;

}