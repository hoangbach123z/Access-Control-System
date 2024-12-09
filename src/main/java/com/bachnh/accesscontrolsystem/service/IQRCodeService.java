package com.bachnh.accesscontrolsystem.service;

import com.bachnh.accesscontrolsystem.data.ResponseData;
import com.bachnh.accesscontrolsystem.dto.request.QrCodeContent;
import com.bachnh.accesscontrolsystem.dto.request.QrCodeRequest;
import com.bachnh.accesscontrolsystem.dto.response.DocRenderResponse;
import com.bachnh.accesscontrolsystem.dto.response.QrCodeResponse;

public interface IQRCodeService {
    ResponseData<DocRenderResponse>generateQRCode(QrCodeRequest request);
    ResponseData<QrCodeResponse> readQRCode(QrCodeContent request);

}
