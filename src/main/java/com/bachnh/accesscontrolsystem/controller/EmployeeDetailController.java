package com.bachnh.accesscontrolsystem.controller;

import com.bachnh.accesscontrolsystem.entity.Employee;
import com.bachnh.accesscontrolsystem.repository.EmployeeRepository;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class EmployeeDetailController implements Initializable {
    @FXML
    MFXTextField txtEmpCode;
    @FXML
    StackPane imgQrCode;
    @FXML
    ImageView imgProfile;
    @Autowired
    EmployeeRepository employeeRepository;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void handleView(String employeeCode) {
        Employee data = employeeRepository.findByEmployeecode(employeeCode);
        String filePath = data.getUrlQrCode().replace("\\", "/"); // Thay \ thành / để tương thích URL
        String fileUrl = "file:///" + filePath; // Thêm tiền tố file://

        // Tạo Image từ URL
        Image qrCode = new Image(fileUrl);

        // Tạo ImageView để hiển thị ảnh
        ImageView imgViewQrCode = new ImageView(qrCode);
        imgViewQrCode.setFitHeight(180); // Đặt chiều cao
        imgViewQrCode.setFitWidth(180);  // Đặt chiều rộng
        imgViewQrCode.setPreserveRatio(true); // Duy trì tỉ lệ ảnh

        // Thêm ImageView vào container
        imgQrCode.getChildren().add(imgViewQrCode);
        txtEmpCode.setText(data.getEmployeecode());


    }

}
