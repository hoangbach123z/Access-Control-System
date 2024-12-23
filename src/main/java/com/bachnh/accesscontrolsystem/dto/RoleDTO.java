package com.bachnh.accesscontrolsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
public class RoleDTO {

    private String ID;

    private String roleCode;

    private String roleName;

    private String status;

    private String createDate;

    private String updateDate;
}
