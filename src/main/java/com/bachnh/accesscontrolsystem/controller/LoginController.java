package com.bachnh.accesscontrolsystem.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    MFXTextField usernameTxt;
    @FXML
    MFXPasswordField passwordTxt;
    @FXML
    MFXButton loginBtn;
    @FXML
    MFXButton cancelBtn;
    public LoginController(Stage stage) {

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login();
    }
    private void login() {
        loginBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                // Đóng cửa sổ hiện tại
                Stage stage = (Stage) loginBtn.getScene().getWindow();
                stage.close();

                // Load giao diện Dashboard từ file FXML
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
                fxmlLoader.setControllerFactory(c->new DashboardController(stage));
                Parent root = fxmlLoader.load(); // Load file FXML và gán cho root

                // Tạo scene mới
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Admin Dashboard");

                // Optional: Đặt icon cho cửa sổ
                // stage.getIcons().add(new Image("/asset/icon.png"));

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Thêm sự kiện cho nút cancel
        cancelBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());

    }

    // Hàm hiển thị dialog lỗi
}
