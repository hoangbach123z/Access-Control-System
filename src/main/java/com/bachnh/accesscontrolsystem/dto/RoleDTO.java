package com.bachnh.accesscontrolsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class RoleDTO {
//    private static final AtomicInteger count = new AtomicInteger(1);


    private final int ID;

    private String roleCode;

    private String roleName;

    private String status;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    public RoleDTO(int ID,String roleCode, String roleName, String status, LocalDateTime createDate, LocalDateTime updateDate) {
        this.ID = ID;
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.status = status;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

}
