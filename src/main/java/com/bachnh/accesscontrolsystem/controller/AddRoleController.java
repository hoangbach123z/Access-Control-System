package com.bachnh.accesscontrolsystem.controller;

import com.bachnh.accesscontrolsystem.entity.Role;
import com.bachnh.accesscontrolsystem.repository.RoleRepository;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
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
public class AddRoleController implements Initializable {
    @Autowired
    private RoleRepository roleRepository;
    @FXML
    MFXTextField txtRoleName;
    @FXML
    MFXTextField txtRoleCode;
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
        if (roleRepository == null) {
            return;
        }
        String roleName = txtRoleName.getText();
        String roleCode = txtRoleCode.getText();
        ErrorMessage message = new ErrorMessage();
        if (roleCode == null || roleCode.trim().isEmpty()) {
            message.errorMessage("Mã chức vụ không được để trống");
            return;
        }
        if (roleName == null || roleName.trim().isEmpty()) {
            message.errorMessage("Tên chức vụ không được để trống");
            return;
        }
        Role role = roleRepository.findByRoleCode(roleCode);
        if (Objects.nonNull(role)) {
            message.errorMessage("Chức vụ đã tồn tại");
        }
        else {
            Role data = new Role();
            data.setID(UUID.randomUUID());
            data.setRoleCode(roleCode);
            data.setRoleName(roleName);
            data.setStatus("Đang hoạt động");
            data.setCreateDate(LocalDateTime.now());
            roleRepository.save(data);
            Stage stage = (Stage) saveBtn.getScene().getWindow(); // Lấy Stage từ nút
            stage.close();
        }
    }
    private void handleCancel() {
        Stage stage = (Stage) saveBtn.getScene().getWindow(); // Lấy Stage từ nút
        stage.close();
    }
}
