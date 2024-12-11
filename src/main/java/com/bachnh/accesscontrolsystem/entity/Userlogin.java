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
@Table(name = "userlogin")
public class Userlogin {
    @Id
    @Column(name = "pk_id", nullable = false)
    private String id;

    @Column(name = "c_username")
    private String username;

    @Column(name = "c_password", length = Integer.MAX_VALUE)
    private String password;

    @Column(name = "c_fullname", length = 50)
    private String fullname;

    @Column(name = "c_status", length = 50)
    private String status;

    @Column(name = "c_create_date")
    private Instant createDate;

    @Column(name = "c_update_date")
    private Instant updateDate;

}