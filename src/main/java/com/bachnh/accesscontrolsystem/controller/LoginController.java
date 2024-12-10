package com.bachnh.accesscontrolsystem.controller;

import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private final Stage stage;
    private double xOffset;
    private double yOffset;
    @FXML
    HBox loginHeader;
    @FXML
    MFXTextField usernameTxt;
    @FXML
    MFXPasswordField passwordTxt;
    @FXML
    MFXButton loginBtn;
    @FXML
    MFXButton cancelBtn;
    @FXML
    FontIcon closeIcon;
    public LoginController(Stage stage) {
        this.stage = stage;
        CSSFX.start();
        UserAgentBuilder.builder()
                .themes(JavaFXThemes.CASPIAN)
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login();
    }
    private void login() {
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());
        loginHeader.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        loginHeader.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
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
