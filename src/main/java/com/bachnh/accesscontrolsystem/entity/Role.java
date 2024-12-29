package com.bachnh.accesscontrolsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id
//    @ColumnDefault("gen_random_uuid()")
    @Column(name = "pk_id", nullable = false)
    private UUID ID;

    @Size(max = 255)
    @Column(name = "c_role_code")
    private String roleCode;

    @Size(max = 255)
    @Column(name = "c_role_name")
    private String roleName;

    @Size(max = 50)
    @Column(name = "c_status", length = 50)
    private String status;

    @Column(name = "c_create_date")
    private LocalDateTime createDate;

    @Column(name = "c_update_date")
    private LocalDateTime updateDate;

}