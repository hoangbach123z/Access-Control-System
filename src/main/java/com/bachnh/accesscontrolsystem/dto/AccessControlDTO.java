package com.bachnh.accesscontrolsystem.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
@Getter
@Setter
@AllArgsConstructor
public class AccessControlDTO {
    private int ID;

    private String code;

    private String fullName;

    private String gender;

//    private String cardID;

    private String departmentName;

    private String roleName;

    private String type;

    private String status;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;


}
