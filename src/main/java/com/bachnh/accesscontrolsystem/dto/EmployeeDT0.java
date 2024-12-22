package com.bachnh.accesscontrolsystem.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeDT0 {
    private String ID;

    private String employeecode;

    private String fullname;

    private String gender;

    private String birthday;

    private String cardId;

    private String mobile;

    private String email;

    private String address;

//    private String cProfileImage;
//
//    private String cQrCode;

    private String departmentName;

    private String roleName;

    private String status;

    private String createDate;

    private String updateDate;
}
