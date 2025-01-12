package com.bachnh.accesscontrolsystem.service.impl;

import com.bachnh.accesscontrolsystem.data.ResponseData;
import com.bachnh.accesscontrolsystem.dto.request.QrCodeContent;
import com.bachnh.accesscontrolsystem.dto.response.QrCodeResponse;
import com.bachnh.accesscontrolsystem.entity.Accesscontrol;
import com.bachnh.accesscontrolsystem.entity.Employee;
import com.bachnh.accesscontrolsystem.entity.Guest;
import com.bachnh.accesscontrolsystem.repository.AccessControlRepository;
import com.bachnh.accesscontrolsystem.repository.EmployeeRepository;
import com.bachnh.accesscontrolsystem.repository.GuestRepository;
import com.bachnh.accesscontrolsystem.service.IQRCodeService;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class QRCodeServiceImpl implements IQRCodeService {
    private final static Logger log = LoggerFactory.getLogger(QRCodeServiceImpl.class.getName());
    @Value("${private.secret-key}")
    String secretKey;
    @Autowired ImageService  imageService;
    @Autowired AESService aesService;
    @Autowired GuestRepository guestRepository;
    @Autowired EmployeeRepository employeeRepository;
    @Autowired AccessControlRepository accessControlRepository;

//    private final String uploadDirectory = "resources\\QRCode";
//    src/main/resources/static/images
    private final String empQRDirectory = "D:/Resources/Images/Employees/QRCode";
    private final String guestQRDirectory = "D:/Resources/Images/Guests/QRCode";
    private final String guestPrfDirectory = "D:/Resources/Images/Guests/Profile";
    private final String empPrfDirectory = "D:/Resources/Images/Employees/Profile";

    @Override
    public String generateQRCode(String employeeCode) {
        String logPrefix = "generate QRCode Image";
        log.info(logPrefix + " ------ START ------");
        String uploadDirectory = employeeCode.matches("^[0-9].*") ? guestQRDirectory : empQRDirectory;
        String objectUrl = "";
        try {
                String content = aesService.encrypt(employeeCode,secretKey);
                String imageName = employeeCode + "_QRCODE" +".PNG";
                // Thiết lập các thông số cho QR code
                Map<EncodeHintType, Object> hints = new HashMap<>();
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // Độ chính xác cao nhất
                hints.put(EncodeHintType.MARGIN, 2); // Margin xung quanh QR code
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                // Tạo QR code với độ phân giải cao hơn
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(
                        content,
                        BarcodeFormat.QR_CODE,
                        500,
                        500,
                        hints
                );
                // Tạo QR code với màu tùy chỉnh
                MatrixToImageConfig config = new MatrixToImageConfig(
                        0xFF000000, // Màu foreground (đen)
                        0xFFFFFFFF  // Màu background (trắng)
                );
                // Chuyển đổi thành ảnh với chất lượng cao
                BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);
                // Đọc logo/icon
                BufferedImage icon = ImageIO.read(new ClassPathResource("/assets/icon_qrcode.png").getInputStream());
                // Tính toán kích thước logo (khoảng 20% của QR code)
                int logoWidth = qrImage.getWidth() / 5;
                int logoHeight = qrImage.getHeight() / 5;
                // Scale logo
                BufferedImage scaledIcon = new BufferedImage(logoWidth, logoHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaledIcon.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawImage(icon, 0, 0, logoWidth, logoHeight, null);
                g2d.dispose();
                // Tạo ảnh kết hợp
                BufferedImage combined = new BufferedImage(
                        qrImage.getWidth(),
                        qrImage.getHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );
                // Vẽ với chất lượng cao
                Graphics2D graphics = combined.createGraphics();
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Vẽ QR code
                graphics.drawImage(qrImage, 0, 0, null);
                // Vẽ logo ở giữa
                graphics.drawImage(scaledIcon,
                        (qrImage.getWidth() - logoWidth) / 2,
                        (qrImage.getHeight() - logoHeight) / 2,
                        null
                );
                graphics.dispose();
                String path = uploadDirectory + "/" + employeeCode;
                objectUrl = imageService.saveImageToStorage(path,imageName, combined);
//            imageService.deleteImage(directorySave,path);
                log.info("{} - Generated QR Code: {}", logPrefix, objectUrl);
                log.info(logPrefix + " ------ END ------");
        } catch (Exception ex) {
            log.error("{} - Error: {}", logPrefix, ex.getMessage(), ex);
            throw new RuntimeException("Failed to generate qr code", ex);
        }
        return objectUrl;
    }
    @Override
    public String saveProfileImage(String employeeCode , File selectedFiled){
        String logPrefix = "Saving Profile Image";
        log.info(logPrefix + " ------ START ------");
        String uploadDirectory = employeeCode.matches("^[0-9].*") ? guestPrfDirectory : empPrfDirectory;
        String objectUrl = "";
        String fileName = employeeCode + "_" + "PROFILE";
        try{
            String path = uploadDirectory + "/" + employeeCode;
            objectUrl = imageService.saveFileToStorage(path,fileName,selectedFiled);
            log.info("{} - Saving Profile Image: {}", logPrefix, objectUrl);
            log.info(logPrefix + " ------ END ------");
        }
        catch(Exception e){
            log.error("{} - Error: {}", logPrefix, e.getMessage(), e);
            throw new RuntimeException("Failed to saving profile image", e);
        }
        return objectUrl;
    };
    @Override
    public String readQRCode(String content) {
        String logPrefix = "Read QRCode Image";
        log.info(logPrefix + " ------ START ------");
        try {
            String code = aesService.decrypt(content,secretKey);
            String type = code.matches("^[0-9].*") ? "GUEST" : "EMPLOYEE";
            if (type.equals("GUEST")) {
                Accesscontrol guestCheckedIn = accessControlRepository.findByCodeAndStatus(code,"IN");
                if (Objects.isNull(guestCheckedIn)) {
                    Guest guest = guestRepository.findByGuestCode(code);
                    Accesscontrol data = new Accesscontrol();
                    data.setCode(code);
                    data.setFullName(guest.getGuestName());
                    data.setGender(guest.getGender());
                    data.setDepartmentName("");
                    data.setRoleName("");
                    data.setType(type);
                    data.setStatus("IN");
                    data.setCheckIn(LocalDateTime.now());
                    accessControlRepository.save(data);
                    return "SUCCCESS";
                }
                guestCheckedIn.setCheckOut(LocalDateTime.now());
                guestCheckedIn.setStatus("OUT");
                accessControlRepository.save(guestCheckedIn);
                return "SUCCCESS";
            }

            if (type.equals("EMPLOYEE")) {
                Accesscontrol empCheckedIn = accessControlRepository.findByCodeAndStatus(code,"IN");
               if (Objects.isNull(empCheckedIn)) {
                   Employee employee = employeeRepository.findByEmployeecode(code);
                   Accesscontrol data = new Accesscontrol();
                   data.setCode(code);
                   data.setFullName(employee.getFullname());
                   data.setGender(employee.getGender());
                   data.setDepartmentName(employee.getDepartmentCode());
                   data.setRoleName(employee.getRoleCode());
                   data.setType(type);
                   data.setStatus("IN");
                   data.setCheckIn(LocalDateTime.now());
                   accessControlRepository.save(data);
                   return "SUCCCESS";
               }
               empCheckedIn.setCheckOut(LocalDateTime.now());
               empCheckedIn.setStatus("OUT");
               accessControlRepository.save(empCheckedIn);
            }

        } catch (Exception e) {
            log.error("getContentFromQR exception {}", e.getMessage());
//            throw  new BaseException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

}
