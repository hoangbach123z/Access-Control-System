package com.bachnh.accesscontrolsystem.controller;

import com.bachnh.accesscontrolsystem.entity.User;
import com.bachnh.accesscontrolsystem.entity.Userlogin;
import com.bachnh.accesscontrolsystem.repository.UserLoginRepository;
import com.bachnh.accesscontrolsystem.service.IUserLoginService;
import com.bachnh.accesscontrolsystem.utils.BCryptUtils;
import com.password4j.Argon2Function;
import com.password4j.BcryptFunction;
import com.password4j.Hash;
import com.password4j.Password;
import com.password4j.types.Argon2;
import com.password4j.types.Bcrypt;
import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.kordamp.ikonli.javafx.FontIcon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
@Component
public class LoginController implements Initializable {
    @Value("${argon2.pepper}")
    String pepper ;
    @Value("${argon2.salt}")
    String salt ;
    @Autowired
    private UserLoginRepository userLoginRepository;
    private  Stage stage;
    private double xOffset;
    private double yOffset;
    @FXML
    BorderPane loginBorderPane;
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
    @FXML
    Label loginMessageLabel;
//    public LoginController(Stage stage) {
//        this.stage = stage;
//        CSSFX.start();
//        UserAgentBuilder.builder()
//                .themes(JavaFXThemes.CASPIAN)
//                .themes(MaterialFXStylesheets.forAssemble(true))
//                .setDeploy(true)
//                .setResolveAssets(true)
//                .build()
//                .setGlobal();
//
//    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cancelButtonAction();
        closeButtonAction();
        loginButtonAction();
        draggedAction();
    }
    private void draggedAction() {
//        Stage stage = (Stage) loginBorderPane.getScene().getWindow();
//        loginHeader.setOnMousePressed(event -> {
//            xOffset = stage.getX() - event.getScreenX();
//            yOffset = stage.getY() - event.getScreenY();
//        });
//
//        loginHeader.setOnMouseDragged(event -> {
//            stage.setX(event.getScreenX() + xOffset);
//            stage.setY(event.getScreenY() + yOffset);
//        });
    }

    // Hàm hiển thị dialog lỗi
    public void cancelButtonAction() {
        cancelBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> Platform.exit());    }
    public void closeButtonAction() {
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> Platform.exit());    }
    public void loginButtonAction() {
        loginBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(usernameTxt.getText().isEmpty() && passwordTxt.getText().isEmpty()) {
                loginMessageLabel.setText("Bắt buộc phải nhập tài khoản và mật khẩu");
            }
            else if (usernameTxt.getText().isEmpty()) {
                loginMessageLabel.setText("Vui lòng điền tên tài khoản");
            }
            else if (passwordTxt.getText().isEmpty()) {
                loginMessageLabel.setText("Vui lòng điền mật khẩu");
            }
            else {
                Userlogin userLogin = userLoginRepository.findByUsername(usernameTxt.getText());
                if(userLogin != null ) {
                    if (verifiedPassword(passwordTxt.getText(),userLogin.getPassword())){
                        //Đóng cửa sổ hiện tại
                        Stage stage = (Stage) loginBtn.getScene().getWindow();
                        stage.close();
                        // Load giao diện Dashboard từ file FXML
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
                        fxmlLoader.setControllerFactory(c->new DashboardController(stage));
                        Parent root = null; // Load file FXML và gán cho root
                        try {
                            root = fxmlLoader.load();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.setTitle("Admin Dashboard");
                        stage.getIcons().add(new Image("/assets/icon.png"));
                        stage.show();
                    }
                    else {
                        loginMessageLabel.setText("Mật khẩu không đúng");
                    }

                }
                else
                {
                    loginMessageLabel.setText("Tài khoản không tồn tại");
                }
            }

        });
    }
    public String hashPassword(String password) {
        Argon2Function argon2 = Argon2Function.getInstance(14, 20, 1, 32, Argon2.ID);
        Hash hash = Password.hash(password)
                .addPepper(pepper)
                .addSalt(salt)
                .with(argon2);

        return hash.getResult();
    }
    public  boolean verifiedPassword(String password,String hash) {
        Argon2Function argon2 = Argon2Function.getInstance(14, 20, 1, 32, Argon2.ID);
        boolean verified = Password.check(password, hash)
                .addPepper(pepper)
                .with(argon2);
        if (verified) {
            return true;
        }
        return false;
    }
}
