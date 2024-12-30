package com.bachnh.accesscontrolsystem.controller;

import com.bachnh.accesscontrolsystem.entity.Guest;
import com.bachnh.accesscontrolsystem.repository.GuestRepository;
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
import java.util.ResourceBundle;
@Component
public class AddGuestController implements Initializable {
    @Autowired
    private GuestRepository guestRepository;
    @FXML
    MFXButton cancelBtn;
    @FXML
    MFXButton saveBtn;
    @FXML
    MFXTextField txtCardID;
    @FXML
    MFXTextField txtGuestName;
    @FXML
    MFXTextField txtPhoneNumber;
    @FXML
    MFXTextField txtEmail;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        saveBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleSave());
        cancelBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleCancel() );
    }
    private void handleSave() {
        if (guestRepository == null) {
            return;
        }
        String cardID = txtCardID.getText();
        String guestName = txtGuestName.getText();
        String phoneNumber = txtPhoneNumber.getText();
        String email = txtEmail.getText();
        ErrorMessage message = new ErrorMessage();
        if (cardID == null || cardID.trim().isEmpty()) {
            message.errorMessage("CMND/CCCD không được để trống");
            return;
        }
        if (guestName == null || guestName.trim().isEmpty()) {
            message.errorMessage("Họ và tên  không được để trống");
            return;
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            message.errorMessage("Số điện thoại không được để trống");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            message.errorMessage("Email không được để trống");
            return;
        }
        Guest data = new Guest();
        data.setCardId(cardID);
        data.setGuestCode("G".concat(cardID));
        data.setGuestName(guestName);
        data.setMobile(phoneNumber);
        data.setEmail(email);
        data.setStatus("Đang hoạt động");
        data.setCreateDate(LocalDateTime.now());
        guestRepository.save(data);
        Stage stage = (Stage) saveBtn.getScene().getWindow(); // Lấy Stage từ nút
        stage.close();
    }
    private void handleCancel() {
        Stage stage = (Stage) saveBtn.getScene().getWindow(); // Lấy Stage từ nút
        stage.close();
    }
}
