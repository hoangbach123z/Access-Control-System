package com.bachnh.accesscontrolsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDTO {


    private int ID;


    private String departmentCode;


    private String departmentName;


    private String status;


    private LocalDateTime createDate;


    private LocalDateTime updateDate;
}
