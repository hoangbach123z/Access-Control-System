package com.bachnh.accesscontrolsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
public class GuestDTO {

    private String ID;

    private String registerCode;

    private String registerName;

    private String cardId;

    private String mobie;

    private String email;

    private String address;

    private String status;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;
}
