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
    private static final AtomicInteger count = new AtomicInteger(0);
    private String ID;

    private String code;

    private String fullName;

    private String gender;

    private String cardID;

    private String departmentName;

    private String roleName;

    private String type;

    private String status;

    private String checkIn;

    private String checkOut;

//    public AccessControlDTO(int id, String cCode, String cFullName, String cCardId, String cDepartmentName, String cRoleName, String cType, String cStatus, LocalDateTime cCheckIn, LocalDateTime cCheckOut) {
//        this.id = count.incrementAndGet();
//        this.cCode = cCode;
//        this.cFullName = cFullName;
//        this.cCardId = cCardId;
//        this.cDepartmentName = cDepartmentName;
//        this.cRoleName = cRoleName;
//        this.cType = cType;
//        this.cStatus = cStatus;
//        this.cCheckIn = cCheckIn;
//        this.cCheckOut = cCheckOut;
//    }
}
