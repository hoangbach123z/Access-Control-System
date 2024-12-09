package com.bachnh.accesscontrolsystem.controller;


import com.bachnh.accesscontrolsystem.MFXDemoResourcesLoader;
import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoader;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoaderBean;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.bachnh.accesscontrolsystem.MFXDemoResourcesLoader.loadURL;

public class DashboardController implements Initializable {
    private final Stage stage;
    private double xOffset;
    private double yOffset;
    private final ToggleGroup toggleGroup;

    @FXML
    private HBox windowHeader;

    @FXML
    private FontIcon closeIcon;

    @FXML
    private FontIcon minimizeIcon;

    @FXML
    private FontIcon maximizeIcon;

    @FXML
    private MFXButton signoutIcon;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private MFXScrollPane scrollPane;

    @FXML
    private VBox navBar;

    @FXML
    private StackPane contentPane;

    @FXML
    private StackPane logoContainer;

    public DashboardController(Stage stage) {
        this.stage = stage;
        this.toggleGroup = new ToggleGroup();
        ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
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
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> ((Stage) rootPane.getScene().getWindow()).setIconified(true));
        maximizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            if(stage.isMaximized()){
                stage.setMaximized(false);
            }else {
                stage.setMaximized(true);
            }
        });

        windowHeader.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        windowHeader.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
        signoutIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                // Đóng cửa sổ hiện tại
                Stage stage = (Stage) signoutIcon.getScene().getWindow();
                stage.close();

                // Load giao diện Dashboard từ file FXML
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
                fxmlLoader.setControllerFactory(c->new LoginController(stage));
                Parent root = fxmlLoader.load(); // Load file FXML và gán cho root
                // Tạo scene mới
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        initializeLoader();
        ScrollUtils.addSmoothScrolling(scrollPane);
        Image image = new Image(MFXDemoResourcesLoader.load("/logo_qr.png"), 100, 100, true, true);
        ImageView logo = new ImageView(image);
        Circle clip = new Circle(50);
        clip.centerXProperty().bind(logo.layoutBoundsProperty().map(Bounds::getCenterX));
        clip.centerYProperty().bind(logo.layoutBoundsProperty().map(Bounds::getCenterY));
        logo.setClip(clip);
        logoContainer.getChildren().add(logo);
    }
    private void initializeLoader() {

        MFXLoader loader = new MFXLoader();
        loader.addView(MFXLoaderBean.of("HOME", loadURL("/fxml/Home.fxml")).setBeanToNodeMapper(() -> createToggle("fas-house", "Trang Chủ")).setDefaultRoot(true).get());
        loader.addView(MFXLoaderBean.of("ACCESSCONTROL", loadURL("/fxml/AccessControl.fxml")).setBeanToNodeMapper(() -> createToggle("fas-chart-column", "Quản lý ra vào")).get());
        loader.addView(MFXLoaderBean.of("EMPLOYEES", loadURL("/fxml/Employees.fxml")).setBeanToNodeMapper(() -> createToggle("fas-address-card", "Quản lý nhân viên")).get());
        loader.addView(MFXLoaderBean.of("REGISTER", loadURL("/fxml/Guests.fxml")).setBeanToNodeMapper(() -> createToggle("fas-building-user", "Quản lý khách ")).get());
        loader.addView(MFXLoaderBean.of("DEPARTMENTS", loadURL("/fxml/Deparments.fxml")).setBeanToNodeMapper(() -> createToggle("fas-building-columns", "Quản lý Phòng ban")).get());
        loader.addView(MFXLoaderBean.of("ROLES", loadURL("/fxml/Roles.fxml")).setBeanToNodeMapper(() -> createToggle("fas-award", "Quản lý chức vụ")).get());
        loader.setOnLoadedAction(beans -> {
            List<ToggleButton> nodes = beans.stream()
                    .map(bean -> {
                        ToggleButton toggle = (ToggleButton) bean.getBeanToNodeMapper().get();
                        toggle.setOnAction(event -> contentPane.getChildren().setAll(bean.getRoot()));
                        if (bean.isDefaultView()) {
                            contentPane.getChildren().setAll(bean.getRoot());
                            toggle.setSelected(true);
                        }
                        return toggle;
                    })
                    .toList();
            navBar.getChildren().setAll(nodes);
        });
        loader.start();
    }
    private ToggleButton createToggle(String icon, String text) {
        return createToggle(icon, text, 0);
    }

    private ToggleButton createToggle(String icon, String text, double rotate) {
        MFXIconWrapper wrapper = new MFXIconWrapper(icon, 24, 32);
        MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode(text, wrapper);
        toggleNode.setAlignment(Pos.CENTER_LEFT);
        toggleNode.setMaxWidth(Double.MAX_VALUE);
        toggleNode.setToggleGroup(toggleGroup);
        if (rotate != 0) wrapper.getIcon().setRotate(rotate);
        return toggleNode;
    }
}
