package com.bachnh.accesscontrolsystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeContent implements Serializable {
    @NotBlank(message = "dataBase64 bắt buộc phải truyền vào")
    private String dataBase64;
}
