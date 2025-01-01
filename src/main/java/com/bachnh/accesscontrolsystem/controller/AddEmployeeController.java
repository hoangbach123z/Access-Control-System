package com.bachnh.accesscontrolsystem.controller;

import com.bachnh.accesscontrolsystem.dto.DepartmentDTO;
import com.bachnh.accesscontrolsystem.entity.Department;
import com.bachnh.accesscontrolsystem.entity.Role;
import com.bachnh.accesscontrolsystem.repository.DepartmentRepository;
import com.bachnh.accesscontrolsystem.repository.EmployeeRepository;
import com.bachnh.accesscontrolsystem.repository.RoleRepository;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class AddEmployeeController implements Initializable {
    @FXML
    MFXDatePicker birthdayDatePicker;
    @FXML
    MFXTextField txtEmpName;
    @FXML
    MFXTextField txtCardID;
    @FXML
    MFXTextField txtPhoneNumber;
    @FXML
    MFXTextField txtEmail;
    @FXML
    MFXTextField txtAddress;
    @FXML
    MFXComboBox<String> cbbDepartment;
    @FXML
    MFXComboBox<String> cbbRole;
    @FXML
    MFXRadioButton txtMale;
    @FXML
    MFXRadioButton txtFemale;
    @FXML
    MFXButton choosenBtn;
    @FXML
    Label lblFileChoosen;
    @FXML
    MFXButton cancelBtn;
    @FXML
    MFXButton saveBtn;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    EmployeeRepository employeeRepository;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        birthdayDatePicker.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", birthdayDatePicker.getLocale()));
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
        saveBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleSave());
        cancelBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleCancel() );
    }

    private void handleSave(){

        String employeeName = txtEmpName.getText();
        String cardID = txtCardID.getText();
        String birthday = birthdayDatePicker.getValue().toString();
        String phoneNumber = txtPhoneNumber.getText();
        String email = txtEmail.getText();
        String address = txtAddress.getText();
        String department = cbbDepartment.getValue();
        String role = cbbRole.getValue();



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
        System.out.println(department);





    }
    private void handleCancel(){
        Stage stage = (Stage) saveBtn.getScene().getWindow(); // Lấy Stage từ nút
        stage.close();
    }
}
