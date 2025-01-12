package com.bachnh.accesscontrolsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@SqlResultSetMapping(
        name = "EmployeeModelMapping",
        classes = @ConstructorResult(
                targetClass = EmployeeModel.class,
                columns = {
                        @ColumnResult(name = "ID", type = UUID.class),
                        @ColumnResult(name = "employeeCode", type = String.class),
                        @ColumnResult(name = "fullname", type = String.class),
                        @ColumnResult(name = "gender", type = String.class),
                        @ColumnResult(name = "birthday", type = LocalDate.class),
                        @ColumnResult(name = "cardID", type = String.class),
                        @ColumnResult(name = "mobile", type = String.class),
                        @ColumnResult(name = "email", type = String.class),
                        @ColumnResult(name = "address", type = String.class),
                        @ColumnResult(name = "departmentName", type = String.class),
                        @ColumnResult(name = "roleName", type = String.class),
                        @ColumnResult(name = "status", type = String.class),
                        @ColumnResult(name = "createDate", type = LocalDateTime.class),
                        @ColumnResult(name = "updateDate", type = LocalDateTime.class)
                }
        )
)

public class EmployeeModel {
    @Id
    @Column(name = "ID")
    private UUID ID;

    @Column(name = "employeeCode")
    private String employeeCode;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "gender")
    private String gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "cardID")
    private String cardID;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "departmentName")
    private String departmentName;

    @Column(name = "roleName")
    private String roleName;

    @Column(name = "status")
    private String status;

    @Column(name = "createDate")
    private LocalDateTime createDate;

    @Column(name = "updateDate")
    private LocalDateTime updateDate;

    public EmployeeModel(UUID ID, String employeeCode, String fullname, String gender, LocalDate birthday, String cardID, String mobile, String email, String address, String departmentName, String roleName, String status, LocalDateTime createDate, LocalDateTime updateDate) {
        this.ID = ID;
        this.employeeCode = employeeCode;
        this.fullname = fullname;
        this.gender = gender;
        this.birthday = birthday;
        this.cardID = cardID;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
        this.departmentName = departmentName;
        this.roleName = roleName;
        this.status = status;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public EmployeeModel(UUID ID, String employeeCode, String fullname, String gender, LocalDate birthday, String cardID, String mobile, String email, String address, String departmentName, String roleName, String status, LocalDateTime updateDate) {
        this.ID = ID;
        this.employeeCode = employeeCode;
        this.fullname = fullname;
        this.gender = gender;
        this.birthday = birthday;
        this.cardID = cardID;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
        this.departmentName = departmentName;
        this.roleName = roleName;
        this.status = status;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public EmployeeModel() {

    }
}
