package com.bachnh.accesscontrolsystem.service;

import com.bachnh.accesscontrolsystem.data.ResponseData;
import com.bachnh.accesscontrolsystem.dto.request.QrCodeContent;
import com.bachnh.accesscontrolsystem.dto.request.QrCodeRequest;
import com.bachnh.accesscontrolsystem.dto.response.DocRenderResponse;
import com.bachnh.accesscontrolsystem.dto.response.QrCodeResponse;

import java.io.File;

public interface IQRCodeService {
    String generateQRCode(String employeeCode);
    String saveProfileImage(String employeeCode , File selectedFiled);
    ResponseData<QrCodeResponse> readQRCode(QrCodeContent request);

}
