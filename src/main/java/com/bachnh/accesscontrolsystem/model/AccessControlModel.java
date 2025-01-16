package com.bachnh.accesscontrolsystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@SqlResultSetMapping(
        name = "AccessControlModelMapping",
        classes = @ConstructorResult(
                targetClass = AccessControlModel.class,
                columns = {
                        @ColumnResult(name = "\"ID\"", type = UUID.class),
                        @ColumnResult(name = "code", type = String.class),
                        @ColumnResult(name = "fullname", type = String.class),
                        @ColumnResult(name = "gender", type = String.class),
                        @ColumnResult(name = "departmentName", type = String.class),
                        @ColumnResult(name = "roleName", type = String.class),
                        @ColumnResult(name = "type", type = String.class),
                        @ColumnResult(name = "status", type = String.class),
                        @ColumnResult(name = "checkIn", type = LocalDateTime.class),
                        @ColumnResult(name = "checkOut", type = LocalDateTime.class)
                }
        )
)
public class AccessControlModel {
    @Id
    @Column(name = "ID")
    private UUID ID;

    @Column(name = "code")
    private String code ;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "gender")
    private String gender;

    @Column(name = "departmentName")
    private String departmentName;

    @Column(name = "roleName")
    private String roleName;

    @Column(name = "type")
    private String type ;

    @Column(name = "status")
    private String status;

    @Column(name = "checkIn")
    private LocalDateTime checkIn;

    @Column(name = "checkOut")
    private LocalDateTime checkOut;

    public AccessControlModel(UUID ID, String code, String fullname, String gender, String departmentName, String roleName, String type, String status, LocalDateTime checkIn, LocalDateTime checkOut) {
        this.ID = ID;
        this.code = code;
        this.fullname = fullname;
        this.gender = gender;
        this.departmentName = departmentName;
        this.roleName = roleName;
        this.type = type;
        this.status = status;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public AccessControlModel() {}
}
