package com.bachnh.accesscontrolsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
public class GuestDTO {

    private int ID;

    private String guestCode;

    private String guestName;

    private String gender;

    private String cardId;

    private LocalDate birthday;

    private String mobile;

    private String email;

    private String address;

    private String status;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;
}
