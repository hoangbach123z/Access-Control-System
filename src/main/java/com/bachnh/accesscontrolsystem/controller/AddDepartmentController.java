package com.bachnh.accesscontrolsystem.controller;

import com.bachnh.accesscontrolsystem.entity.Department;
import com.bachnh.accesscontrolsystem.repository.DepartmentRepository;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

@Component
public class AddDepartmentController implements Initializable {
    @Autowired
    private DepartmentRepository departmentRepository;
    @FXML
    MFXTextField txtDepartmentName;
    @FXML
    MFXTextField txtDepartmentCode;
    @FXML
    MFXButton saveBtn;
    @FXML
    MFXButton cancelBtn;;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        saveBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleSave());
        cancelBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleCancel() );
    }

    private void handleSave() {
        if (departmentRepository == null) {
            return;
        }
        String departmentCode = txtDepartmentCode.getText();
        String departmentName = txtDepartmentName.getText();
        ErrorMessage message = new ErrorMessage();
        if (departmentCode == null || departmentCode.trim().isEmpty()) {
            message.errorMessage("Mã phòng ban không được để trống");
            return;
        }
        if (departmentName == null || departmentName.trim().isEmpty()) {
            message.errorMessage("Tên phòng ban không được để trống");
            return;
        }
        Department department = departmentRepository.findByDepartmentCode(departmentCode);
        if (Objects.nonNull(department)) {
            message.errorMessage("Chức vụ đã tồn tại");
        }
        else {
            Department data = new Department();
            data.setID(UUID.randomUUID());
            data.setDepartmentCode(departmentCode);
            data.setDepartmentName(departmentName);
            data.setStatus("Đang hoạt động");
            data.setCreateDate(LocalDateTime.now());
            departmentRepository.save(data);
            Stage stage = (Stage) saveBtn.getScene().getWindow(); // Lấy Stage từ nút
            stage.close();
        }
    }
    private void handleCancel() {
        Stage stage = (Stage) saveBtn.getScene().getWindow(); // Lấy Stage từ nút
        stage.close();
    }
}
