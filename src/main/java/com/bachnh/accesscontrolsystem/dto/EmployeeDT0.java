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
    private int ID;

    private String employeeCode;

    private String fullname;

    private String gender;

    private LocalDate birthday;

    private String cardID;

    private String mobile;

    private String email;

    private String address;

    private String cProfileImage;

    private String cQrCode;

    private String departmentName;

    private String roleName;

    private String status;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    public EmployeeDT0(int ID, String employeeCode, String fullname, String gender, LocalDate birthday, String cardID, String mobile, String email, String address, String status, LocalDateTime createDate, LocalDateTime updateDate) {
        this.ID = ID;
        this.employeeCode = employeeCode;
        this.fullname = fullname;
        this.gender = gender;
        this.birthday = birthday;
        this.cardID = cardID;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
        this.status = status;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
}
