package com.bachnh.accesscontrolsystem.controller;

import com.bachnh.accesscontrolsystem.entity.Role;
import com.bachnh.accesscontrolsystem.repository.RoleRepository;
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
    MFXButton cancelBtn;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        saveBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> save());
        saveBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> save());
    }

    public void save() {
        if (roleRepository == null) {
            return;
        }
        String roleName = txtRoleName.getText();
        String roleCode = txtRoleCode.getText();

// Kiểm tra nếu roleCode bị null hoặc rỗng
        if (roleCode == null || roleCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã chức vụ không được để trống");
        }

        Role role = roleRepository.findByRoleCode(roleCode);

// Nếu role đã tồn tại, ném lỗi
        if (Objects.nonNull(role)) {
            throw new RuntimeException("Chức vụ đã tồn tại");
        }

// Nếu role chưa tồn tại, tạo mới
        Role data = new Role();
        data.setID(UUID.randomUUID());
        data.setRoleCode(roleCode);
        data.setRoleName(roleName);
        data.setStatus("Đang hoạt động");
        data.setCreateDate(LocalDateTime.now());

// Lưu vào cơ sở dữ liệu
        roleRepository.save(data);
        Stage stage = (Stage) saveBtn.getScene().getWindow(); // Lấy Stage từ nút
        stage.close();
    }
}
