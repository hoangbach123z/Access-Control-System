package com.bachnh.accesscontrolsystem.controller;

import com.bachnh.accesscontrolsystem.entity.Department;
import com.bachnh.accesscontrolsystem.entity.Employee;
import com.bachnh.accesscontrolsystem.entity.Role;
import com.bachnh.accesscontrolsystem.repository.CommonAdapter;
import com.bachnh.accesscontrolsystem.repository.DepartmentRepository;
import com.bachnh.accesscontrolsystem.repository.EmployeeRepository;
import com.bachnh.accesscontrolsystem.repository.RoleRepository;
import com.bachnh.accesscontrolsystem.service.IQRCodeService;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.utils.DateTimeUtils;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class AddEmployeeController implements Initializable {
    @FXML AnchorPane rootPane;
    @FXML MFXDatePicker birthdayDatePicker;
    @FXML MFXTextField txtEmpName;
    @FXML MFXTextField txtCardID;
    @FXML MFXTextField txtPhoneNumber;
    @FXML MFXTextField txtEmail;
    @FXML MFXTextField txtAddress;
    @FXML MFXComboBox<String> cbbDepartment;
    @FXML MFXComboBox<String> cbbRole;
    @FXML MFXRadioButton txtMale;
    @FXML MFXRadioButton txtFemale;
    @FXML MFXButton choosenBtn;
    @FXML Label lblFileChoosen;
    @FXML MFXButton cancelBtn;
    @FXML MFXButton saveBtn;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired EmployeeRepository employeeRepository;
    @Autowired CommonAdapter commonAdapter;
    @Autowired IQRCodeService iqrCodeService;
    private File selectedFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        birthdayDatePicker.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", birthdayDatePicker.getLocale()));
        birthdayDatePicker.setGridAlgorithm(DateTimeUtils::partialIntMonthMatrix);
//        birthdayDatePicker.setText();
        birthdayDatePicker.setYearsRange(NumberRange.of(1990, 2025));

        // xử lý combobox phòng ban
        List<Department> departments = departmentRepository.findAll();
        for (Department department : departments) {
            cbbDepartment.getItems().add(department.getDepartmentName());
        }

        // Xử lý combobox
        List<Role> roles = roleRepository.findAll();
        for (Role role : roles) {
            cbbRole.getItems().add(role.getRoleName());
        }
        ToggleGroup genderGroup = new ToggleGroup();
        // Xử ly toggleGroup gender
        txtFemale.setToggleGroup(genderGroup);
        txtMale.setToggleGroup(genderGroup);
        txtMale.setSelected(true);
        // xử lý upload ảnh
        choosenBtn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            Stage stage = (Stage) rootPane.getScene().getWindow();
            // Mở FileChooser
            selectedFile = fileChooser.showOpenDialog(stage);
            // Nếu người dùng chọn file
            if (selectedFile != null) {
//                try {
//                    // Lưu file vào thư mục lưu trữ
//                    String savedFileName = "";
//
//                    // Hiển thị tên file đã lưu
                    lblFileChoosen.setText("Đã chọn file " );
//                } catch (IOException e) {
//                    lblFileChoosen.setText("Lỗi khi lưu file: " + e.getMessage());
//                    e.printStackTrace();
//                }
            } else {
                lblFileChoosen.setText("Chưa chọn file");
            }

        });
        saveBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleSave());
        cancelBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleCancel() );
    }

    private void handleSave(){

        String employeeName = txtEmpName.getText();
        String cardID = txtCardID.getText();
        String birthday = birthdayDatePicker.getText();
        String phoneNumber = txtPhoneNumber.getText();
        String email = txtEmail.getText();
        String address = txtAddress.getText();
        String department = cbbDepartment.getValue();
        String role = cbbRole.getValue();
        String gender = "";
        ErrorMessage msg = new ErrorMessage();

        cbbDepartment.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cbbDepartment.setValue("");
            }
            else{
                cbbDepartment.setValue(newValue);
            }

        });
        cbbRole.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cbbRole.setValue("");
            }
            else{
                cbbRole.setValue(newValue);
            }
        });

        if (txtMale.isSelected()) {
            gender = "Nam";
        }
        if (txtFemale.isSelected()) {
            gender = "Nũ";
        }
        if (employeeName == null || employeeName.trim().isEmpty()) {
            msg.errorMessage("Họ và tên không được để trống");
            return;
        }
        else if (cardID == null || cardID.trim().isEmpty()) {
            msg.errorMessage("CMND/CCCD không được để trống");
            return;
        }
        else if (birthday == null) {
            msg.errorMessage("Ngày tháng năm sinh không được để trống");
            return;
        }
        else if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            msg.errorMessage("Số điện thoại không được để trống");
            return;
        }
        else if (email == null || email.trim().isEmpty()) {
            msg.errorMessage("Email không được để trống");
            return;
        }
        else if (address == null || address.trim().isEmpty()) {
            msg.errorMessage("Địa chỉ không được để trống");
            return;
        }
        else if(department == null || department.trim().isEmpty()) {
            msg.errorMessage("Phòng ban không được để trống");
            return;
        }
        else if (role == null || role.trim().isEmpty()) {
            msg.errorMessage("Chức vụ không được để trống");
            return;
        }
        else if (gender.trim().isEmpty()) {
            msg.errorMessage("Giới tính không được để trống");
            return;
        }
        else {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Department deptCode = departmentRepository.findByDepartmentName(cbbDepartment.getText().trim());
        Role roleCode = roleRepository.findByRoleName(cbbRole.getText().trim());
        String employeeCode = commonAdapter.generateEmployeeCode(deptCode.getDepartmentCode());
        String urlQRCode = iqrCodeService.generateQRCode(employeeCode);
        String urlProfileImage = iqrCodeService.saveProfileImage(employeeCode,selectedFile);
        System.out.println(employeeCode);
            Employee data = new Employee();
            data.setEmployeecode(employeeCode);
            data.setFullname(employeeName);
            data.setCardId(cardID);
            data.setBirthday(LocalDate.parse(birthday,formatter));
            data.setMobile(phoneNumber);
            data.setEmail(email);
            data.setAddress(address);
            data.setDepartmentCode(deptCode.getDepartmentCode());
            data.setRoleCode(roleCode.getRoleCode());
            data.setGender(gender);
            data.setUrlQrCode(urlQRCode);
            data.setUrlProfileImage(urlProfileImage);
            data.setStatus("Đang hoạt động");
            data.setCreateDate(LocalDateTime.now());
            employeeRepository.save(data);
            Stage stage = (Stage) saveBtn.getScene().getWindow();
            stage.close();
        }





    }
    private void handleCancel(){
        Stage stage = (Stage) saveBtn.getScene().getWindow(); // Lấy Stage từ nút
        stage.close();
    }
}
