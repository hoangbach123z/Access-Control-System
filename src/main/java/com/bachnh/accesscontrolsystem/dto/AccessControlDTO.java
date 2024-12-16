package com.bachnh.accesscontrolsystem.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
@Getter
@Setter
public class AccessControlDTO {
    private static final AtomicInteger count = new AtomicInteger(0);
    private int id;

    private String cCode;

    private String cFullName;

    private String cCardId;

    private String cDepartmentName;

    private String cRoleName;

    private String cType;

    private String cStatus;

    private LocalDateTime cCheckIn;

    private LocalDateTime cCheckOut;

    public AccessControlDTO(int id, String cCode, String cFullName, String cCardId, String cDepartmentName, String cRoleName, String cType, String cStatus, LocalDateTime cCheckIn, LocalDateTime cCheckOut) {
        this.id = count.incrementAndGet();
        this.cCode = cCode;
        this.cFullName = cFullName;
        this.cCardId = cCardId;
        this.cDepartmentName = cDepartmentName;
        this.cRoleName = cRoleName;
        this.cType = cType;
        this.cStatus = cStatus;
        this.cCheckIn = cCheckIn;
        this.cCheckOut = cCheckOut;
    }
}
