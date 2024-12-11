package com.bachnh.accesscontrolsystem.service.impl;

import com.bachnh.accesscontrolsystem.constant.DateConstant;
import com.bachnh.accesscontrolsystem.data.ResponseData;
import com.bachnh.accesscontrolsystem.dto.request.QrCodeContent;
import com.bachnh.accesscontrolsystem.dto.request.QrCodeRequest;
import com.bachnh.accesscontrolsystem.dto.response.DocRenderResponse;
import com.bachnh.accesscontrolsystem.dto.response.QrCodeResponse;
import com.bachnh.accesscontrolsystem.entity.DocumentGenerator;
import com.bachnh.accesscontrolsystem.repository.DocumentGeneratorRepository;
import com.bachnh.accesscontrolsystem.repository.UserRepository;
import com.bachnh.accesscontrolsystem.service.IQRCodeService;
import com.bachnh.accesscontrolsystem.utils.DateUtils;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;

@Service
@Slf4j
public class QRCodeServiceImpl implements IQRCodeService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    DocumentGeneratorRepository documentGeneratorRepository;
    @Autowired
    ImageService  imageService;
    private final String QRCODE_PATH= "QRCode";
    @Override
    public ResponseData<DocRenderResponse> generateQRCode(QrCodeRequest request) {
        String logPrefix = "generateQRCodeImage";
        ResponseData<DocRenderResponse> responseData = new ResponseData<>();
        DocRenderResponse data = new DocRenderResponse();
        String objectUrl = "";
        try {
            DocumentGenerator docGen = documentGeneratorRepository.findByEmployeeCodeAndTemplate(request.getEmployeeCode(),"QR_CODE");
            if (Objects.nonNull(docGen)) {
                objectUrl = docGen.getObjectUrl();
                data.setUrl(objectUrl);
                data.setPath(docGen.getPathFile());
                data.setTime(
                        DateUtils.convertLocalDateToString(DateConstant.STR_PLAN_DD_MM_YYYY,
                                LocalDate.now()));
                responseData.setData(data);
            }else {
                String imageName = request.getEmployeeCode()
                        +".PNG";
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(request.getEmployeeCode(), BarcodeFormat.QR_CODE,
                        request.getWidth(), request.getHeight() );
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
                BufferedImage icon = ImageIO.read(new ClassPathResource("assets/icon.png").getInputStream());
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
                BufferedImage combined =  new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = combined.getGraphics();
                graphics.drawImage(image, 0, 0, null);
                graphics.drawImage(icon, image.getWidth() / 2 - icon.getWidth() / 2,
                        image.getHeight() / 2 - icon.getHeight() / 2, null);
//            String base64String = CommonUtils.imgToBase64String(combined,"png");
//            responseData.setData(base64String);
//            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
//            ImageIO.write(combined, "png", baos2);
                String path = QRCODE_PATH + "\\" + request.getEmployeeCode();
                objectUrl = imageService.saveImageToStorage(path,imageName, combined);
//            imageService.deleteImage(directorySave,path);
                DocumentGenerator documentGenerator =  new DocumentGenerator();
                documentGenerator.setCreateTime(LocalDateTime.now());
                documentGenerator.setUpdateTime(LocalDateTime.now());
                documentGenerator.setEmployeeCode(request.getEmployeeCode());
                documentGenerator.setPathFile(path);
                documentGenerator.setObjectUrl(objectUrl);
                documentGenerator.setStatus(DocumentGenerator.DocumentSyncStatus.ACTIVE.name());
//                documentGenerator.setRequestLog(StringUtils.substring(JsonUtils.toJson(request), 0, 2000));
                documentGenerator.setStorage("SERVER");
                documentGenerator.setTemplate("QR_CODE");
//                documentGenerator.setContentType(MediaType.IMAGE_PNG_VALUE);
                documentGeneratorRepository.save(documentGenerator);
                data.setUrl(objectUrl);
                data.setPath(path);
                data.setTime(
                        DateUtils.convertLocalDateToString(DateConstant.STR_PLAN_DD_MM_YYYY,
                                LocalDate.now()));
                responseData.setData(data);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return responseData;
    }

    @Override
    public ResponseData<QrCodeResponse> readQRCode(QrCodeContent request) {
        ResponseData<QrCodeResponse> responseData = new ResponseData<>();
        QrCodeResponse data = new QrCodeResponse();
        try {
            byte[] imageByte = Base64.getDecoder().decode(request.getDataBase64());
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageByte);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                    new BufferedImageLuminanceSource(ImageIO.read(byteArrayInputStream))
            ));
            Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
            data.setContent(qrCodeResult.getText());
            responseData.setData(data);
        } catch (IOException | NotFoundException e) {
            log.error("getContentFromQR exception {}", e.getMessage());
//            throw  new BaseException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
        return responseData;
    }
}
