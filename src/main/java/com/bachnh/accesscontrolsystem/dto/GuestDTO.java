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

    private String guestCode;

    private String guestName;

    private String gender;

    private String cardId;

    private String birthday;

    private String mobie;

    private String email;

    private String address;

    private String status;

    private String createDate;

    private String updateDate;
}
