package com.bachnh.accesscontrolsystem.controller;

import com.bachnh.accesscontrolsystem.entity.Department;
import com.bachnh.accesscontrolsystem.entity.Employee;
import com.bachnh.accesscontrolsystem.entity.Role;
import com.bachnh.accesscontrolsystem.repository.DepartmentRepository;
import com.bachnh.accesscontrolsystem.repository.EmployeeRepository;
import com.bachnh.accesscontrolsystem.repository.RoleRepository;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
public class EmployeeDetailController implements Initializable {
    @FXML
    AnchorPane anchorContainer;
    @FXML
    MFXTextField txtEmpCode;
    @FXML
    StackPane imgQrCode;
    @FXML
    StackPane imgProfile;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    @FXML
    private MFXTextField txtAddress;

    @FXML
    private MFXTextField txtBirthday;

    @FXML
    private MFXTextField txtCardID;

    @FXML
    private MFXTextField txtDpmName;

    @FXML
    private MFXTextField txtEmail;

    @FXML
    private MFXTextField txtEmpName;

    @FXML
    private MFXTextField txtGender;

    @FXML
    private MFXTextField txtPhoneNumber;

    @FXML
    private MFXTextField txtRoleName;
    @FXML
    private MFXButton closeBtn;
    @Autowired EmployeeRepository employeeRepository;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired RoleRepository roleRepository;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        closeBtn.setOnAction(event -> {
            Stage stage = (Stage) anchorContainer.getScene().getWindow();
            stage.close();
        });
    }
    public void handleView(String employeeCode) {
        Employee data = employeeRepository.findByEmployeecode(employeeCode);
        Department department = departmentRepository.findByDepartmentCode(data.getDepartmentCode());
        Role role = roleRepository.findByRoleCode(data.getRoleCode());
        String fileUrlQRCode = getFormattedFileUrl(data.getUrlQrCode());
        // Tạo Image từ URL
        Image qrCode = new Image(fileUrlQRCode);
        // Tạo ImageView để hiển thị ảnh
        ImageView imgViewQrCode = new ImageView(qrCode);
        imgViewQrCode.setFitHeight(180); // Đặt chiều cao
        imgViewQrCode.setFitWidth(180);  // Đặt chiều rộng
        imgViewQrCode.setPreserveRatio(true); // Duy trì tỉ lệ ảnh
        imgQrCode.getChildren().clear();
        imgQrCode.getChildren().add(imgViewQrCode);

        String fileUrlProfile = getFormattedFileUrl(data.getUrlProfileImage());
        // Tạo Image từ URL
        Image profile = new Image(fileUrlProfile);
        ImageView imgViewProfile = new ImageView(profile);
        imgViewProfile.setFitHeight(160); // Đặt chiều cao
        imgViewProfile.setFitWidth(160);
//        Circle clip = new Circle(160, 160, 160);
//        imgViewProfile.s;// Đặt chiều rộng
        imgViewProfile.setPreserveRatio(true); // Duy trì tỉ lệ ảnh
        // Thêm ImageView vào container
        imgProfile.setAlignment(Pos.CENTER); // Căn giữa nội dung trong StackPane
        StackPane.setAlignment(imgViewProfile, Pos.CENTER);
        imgProfile.getChildren().clear();
        imgProfile.getChildren().add(imgViewProfile);
        txtEmpCode.setText(data.getEmployeecode());
//        lblFullname.setText(data.getFullname());
        txtEmpName.setText(data.getFullname());
        txtDpmName.setText(department.getDepartmentName());
        txtRoleName.setText(role.getRoleName());
//        lblRolename.setText(role.getRoleName());
        txtBirthday.setText((data.getBirthday().format(DATE_FORMATTER)));
        txtGender.setText(data.getGender());
        txtCardID.setText(data.getCardId());
        txtPhoneNumber.setText(data.getMobile());
        txtEmail.setText(data.getEmail());
        txtAddress.setText(data.getAddress());
    }
    public static String getFormattedFileUrl(String url) {
//        if (url == null || url.isEmpty()) {
//            throw new IllegalArgumentException("URL QR Code không được null hoặc rỗng.");
//        }
        String filePath = url.replace("\\", "/"); // Thay \ thành / để tương thích URL
        return "file:///" + filePath; // Thêm tiền tố file://
    }

}
