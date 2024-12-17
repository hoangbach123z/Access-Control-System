package com.bachnh.accesscontrolsystem.controller;


import com.bachnh.accesscontrolsystem.MFXDemoResourcesLoader;
import com.bachnh.accesscontrolsystem.data.ViewData;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.bachnh.accesscontrolsystem.MFXDemoResourcesLoader.loadURL;

public class DashboardController implements Initializable {
    private final Stage stage;
    private double xOffset;
    private double yOffset;
    private final ToggleGroup toggleGroup;
    private static final int LOGO_SIZE = 100;
    private static final int CLIP_RADIUS = 50;
    private static final int ICON_SIZE = 24;
    private static final int WRAPPER_SIZE = 32;

    @FXML private HBox windowHeader;
    @FXML private FontIcon closeIcon;
    @FXML private FontIcon minimizeIcon;
    @FXML private FontIcon maximizeIcon;
    @FXML private MFXButton signoutIcon;
    @FXML private AnchorPane rootPane;
    @FXML private MFXScrollPane scrollPane;
    @FXML private VBox navBar;
    @FXML private StackPane contentPane;
    @FXML private StackPane logoContainer;

    // Cache thường xuyên sử dụng
    private final Map<String, Parent> viewCache = new HashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public DashboardController(Stage stage) {
        this.stage = stage;
        this.toggleGroup = new ToggleGroup();
        initializeUI();
    }

    private void initializeUI() {
        ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);


        // Khởi tạo CSSFX và UserAgent một lần
        Platform.runLater(() -> {
            CSSFX.start();
            UserAgentBuilder.builder()
                    .themes(JavaFXThemes.CASPIAN)
                    .themes(MaterialFXStylesheets.forAssemble(true))
                    .setDeploy(true)
                    .setResolveAssets(true)
                    .build()
                    .setGlobal();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeWindowControls();
        initializeLoader();
        initializeLogo();
        ScrollUtils.addSmoothScrolling(scrollPane);
    }

    private void initializeWindowControls() {
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED,
                event -> ((Stage) rootPane.getScene().getWindow()).setIconified(true));

        maximizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setMaximized(!stage.isMaximized());
        });

        // Window dragging
        windowHeader.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        windowHeader.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

        signoutIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleSignOut);
    }

    private void handleSignOut(MouseEvent event) {
        try {
            Stage stage = (Stage) signoutIcon.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            fxmlLoader.setControllerFactory(c -> new LoginController());

            // Load FXML trong background thread
            executorService.submit(() -> {
                try {
                    Parent root = fxmlLoader.load();
                    Platform.runLater(() -> {
                        stage.setScene(new Scene(root));
                        stage.show();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeLoader() {
        MFXLoader loader = new MFXLoader();

        // Sử dụng LinkedHashMap để duy trì thứ tự các view
        LinkedHashMap<String, ViewData> viewsOrder = new LinkedHashMap<>();
        viewsOrder.put("HOME", new ViewData("/fxml/Home.fxml", "fas-house", "Trang Chủ", true));
        viewsOrder.put("ACCESSCONTROL", new ViewData("/fxml/AccessControl.fxml", "fas-chart-column", "Quản lý ra vào", false));
        viewsOrder.put("EMPLOYEES", new ViewData("/fxml/Employees.fxml", "fas-address-card", "Quản lý nhân viên", false));
        viewsOrder.put("REGISTER", new ViewData("/fxml/Guests.fxml", "fas-building-user", "Quản lý khách", false));
        viewsOrder.put("DEPARTMENTS", new ViewData("/fxml/Deparments.fxml", "fas-building-columns", "Quản lý Phòng ban", false));
        viewsOrder.put("ROLES", new ViewData("/fxml/Roles.fxml", "fas-award", "Quản lý chức vụ", false));

        // Thêm views theo thứ tự
        viewsOrder.forEach((name, data) -> {
            loader.addView(MFXLoaderBean.of(name, loadURL(data.getFxmlPath()))
                    .setBeanToNodeMapper(() -> createToggle(data.getIcon(), data.getText()))
                    .setDefaultRoot(data.isDefault())
                    .get());
        });

        loader.setOnLoadedAction(beans -> {
            List<ToggleButton> nodes = new ArrayList<>();
            // Duy trì thứ tự của các view khi tạo toggle buttons
            viewsOrder.forEach((name, data) -> {
                beans.stream()
                        .filter(bean -> bean.getViewName().equals(name))
                        .findFirst()
                        .ifPresent(bean -> {
                            ToggleButton toggle = (ToggleButton) bean.getBeanToNodeMapper().get();
                            toggle.setOnAction(event -> contentPane.getChildren().setAll(bean.getRoot()));
                            if (bean.isDefaultView()) {
                                contentPane.getChildren().setAll(bean.getRoot());
                                toggle.setSelected(true);
                            }
                            nodes.add(toggle);
                        });
            });
            navBar.getChildren().setAll(nodes);
        });

        loader.start();
    }

    private MFXLoaderBean createLoaderBean(String name, String fxmlPath, String icon, String text, boolean isDefault) {
        return MFXLoaderBean.of(name, loadURL(fxmlPath))
                .setBeanToNodeMapper(() -> createToggle(icon, text))
                .setDefaultRoot(isDefault)
                .get();
    }

    private ToggleButton createToggleFromBean(MFXLoaderBean bean) {
        ToggleButton toggle = (ToggleButton) bean.getBeanToNodeMapper().get();
        toggle.setOnAction(event -> {
            if (!viewCache.containsKey(bean.getViewName())) {
                viewCache.put(bean.getViewName(), bean.getRoot());
            }
            contentPane.getChildren().setAll(viewCache.get(bean.getViewName()));
        });

        if (bean.isDefaultView()) {
            contentPane.getChildren().setAll(bean.getRoot());
            toggle.setSelected(true);
        }
        return toggle;
    }

    private void initializeLogo() {
        Image image = new Image(MFXDemoResourcesLoader.load("/assets/logo_qr.png"),
                LOGO_SIZE, LOGO_SIZE, true, true);
        ImageView logo = new ImageView(image);

        Circle clip = new Circle(CLIP_RADIUS);
        clip.centerXProperty().bind(logo.layoutBoundsProperty().map(Bounds::getCenterX));
        clip.centerYProperty().bind(logo.layoutBoundsProperty().map(Bounds::getCenterY));

        logo.setClip(clip);
        logoContainer.getChildren().add(logo);
    }

    private ToggleButton createToggle(String icon, String text) {
        return createToggle(icon, text, 0);
    }

    private ToggleButton createToggle(String icon, String text, double rotate) {
        MFXIconWrapper wrapper = new MFXIconWrapper(icon, ICON_SIZE, WRAPPER_SIZE);
        MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode(text, wrapper);
        toggleNode.setAlignment(Pos.CENTER_LEFT);
        toggleNode.setMaxWidth(Double.MAX_VALUE);
        toggleNode.setToggleGroup(toggleGroup);
        if (rotate != 0) wrapper.getIcon().setRotate(rotate);
        return toggleNode;
    }

    // Cleanup resources
    public void cleanup() {
        executorService.shutdown();
        viewCache.clear();
    }
}
