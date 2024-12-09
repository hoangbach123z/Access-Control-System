package com.bachnh.accesscontrolsystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeRequest {
    @NotBlank(message = "employeeCode bắt buộc phải truyền vào")
    private String employeeCode;
//    @NotBlank(message = "employeeName bắt buộc phải truyền vào")
//    private String employeeName;
    @Min(10)
    @Max(1000)
    private Integer width = 500;
    @Min(10)
    @Max(1000)
    private Integer height = 500;
}
