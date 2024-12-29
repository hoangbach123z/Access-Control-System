package com.bachnh.accesscontrolsystem.controller;


import com.bachnh.accesscontrolsystem.MFXDemoResourcesLoader;
import com.bachnh.accesscontrolsystem.config.StageManager;
import com.bachnh.accesscontrolsystem.data.ViewData;
import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import io.github.palexdev.materialfx.theming.base.Theme;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoader;
import io.github.palexdev.materialfx.utils.others.loader.MFXLoaderBean;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
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
import lombok.RequiredArgsConstructor;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.bachnh.accesscontrolsystem.MFXDemoResourcesLoader.loadURL;
@Component
@RequiredArgsConstructor
public class DashboardController implements Initializable {

    private final ApplicationContext springBootContext;

//    private final StageManager stage;
    private double xOffset, yOffset;
    private final ToggleGroup toggleGroup = new ToggleGroup();
    private final Map<String, Parent> viewCache = new HashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    @FXML private HBox windowHeader;
    @FXML private FontIcon closeIcon, minimizeIcon, maximizeIcon;
    @FXML private MFXButton signoutIcon;
    @FXML private AnchorPane rootPane;
    @FXML private MFXScrollPane scrollPane;
    @FXML private VBox navBar;
    @FXML private StackPane contentPane, logoContainer;

    private void initializeUI() {
        ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
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

        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setIconified(true);
        });

        maximizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setMaximized(!stage.isMaximized());
        });

        windowHeader.setOnMousePressed(event -> {
            Stage stage = (Stage) windowHeader.getScene().getWindow();
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        windowHeader.setOnMouseDragged(event -> {
            Stage stage = (Stage) windowHeader.getScene().getWindow();
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

        signoutIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> handleSignOut());
    }


    private void handleSignOut() {
        Task<Void> signOutTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.close();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
                loader.setControllerFactory(c -> new LoginController());
                Parent root = loader.load();
                Platform.runLater(() -> {
                    stage.setScene(new Scene(root));
                    stage.show();
                });
                return null;
            }
        };
        executorService.submit(signOutTask);
    }

    private void initializeLoader() {
        MFXLoader loader = new MFXLoader();
        LinkedHashMap<String, ViewData> views = new LinkedHashMap<>() {{
            put("Home", new ViewData("/fxml/Home.fxml", "fas-house", "Trang Chủ", true));
            put("AccessControl", new ViewData("/fxml/AccessControl.fxml", "fas-chart-column", "Quản lý ra vào", false));
            put("Employees", new ViewData("/fxml/Employees.fxml", "fas-address-card", "Quản lý nhân viên", false));
            put("Guests", new ViewData("/fxml/Guests.fxml", "fas-building-user", "Quản lý khách", false));
            put("Deparments", new ViewData("/fxml/Deparments.fxml", "fas-building-columns", "Quản lý Phòng ban", false));
            put("Roles", new ViewData("/fxml/Roles.fxml", "fas-award", "Quản lý chức vụ", false));
        }};

        views.forEach((name, data) -> {
            loader.addView(MFXLoaderBean.of(name, loadURL(data.getFxmlPath()))
                    .setBeanToNodeMapper(() -> createToggle(data.getIcon(), data.getText()))
                    .setDefaultRoot(data.isDefault())
                    .get());
        });

        loader.setOnLoadedAction(beans -> {
            List<ToggleButton> nodes = new ArrayList<>();
            views.forEach((name, data) -> {
                beans.stream()
                        .filter(bean -> bean.getViewName().equals(name))
                        .findFirst()
                        .ifPresent(bean -> {
                            ToggleButton toggle = (ToggleButton) bean.getBeanToNodeMapper().get();
                            toggle.setOnAction(event -> loadView(bean));
                            if (bean.isDefaultView()) {
                                loadView(bean);
                                toggle.setSelected(true);
                            }
                            nodes.add(toggle);
                        });
            });
            navBar.getChildren().setAll(nodes);
        });

        loader.start();
    }

    private void loadView(MFXLoaderBean bean) {
        if (!viewCache.containsKey(bean.getViewName())) {
            contentPane.getChildren().setAll(createLoadingPlaceholder());
            Task<Parent> loadViewTask = new Task<>() {
                @Override
                protected Parent call() throws Exception {
                    String relativePath = "/fxml/" + bean.getViewName() + ".fxml";
                    URL resourceUrl = getClass().getResource(relativePath);
                    FXMLLoader loader = new FXMLLoader(resourceUrl);
                    loader.setControllerFactory(springBootContext::getBean); // Sử dụng Spring
                    return loader.load();
                }
            };

            loadViewTask.setOnSucceeded(event -> {
                Parent view = loadViewTask.getValue();
                viewCache.put(bean.getViewName(), view);
                contentPane.getChildren().setAll(view);
            });

            loadViewTask.setOnFailed(event -> {
                Throwable exception = loadViewTask.getException();
                System.err.println("Failed to load view: " + bean.getViewName());
                exception.printStackTrace();
                contentPane.getChildren().setAll(createLoadingPlaceholder());
            });

            executorService.submit(loadViewTask);
        } else {
            contentPane.getChildren().setAll(viewCache.get(bean.getViewName()));
        }
    }

    private Node createLoadingPlaceholder() {
        MFXProgressSpinner loading = new MFXProgressSpinner();
        StackPane placeholder = new StackPane(loading);
        placeholder.setPrefSize(400, 300);
        return placeholder;
    }

    private void initializeLogo() {
        ImageView logo = new ImageView(new Image(MFXDemoResourcesLoader.load("/assets/logo_qr.png"), 100, 100, true, true));
        Circle clip = new Circle(50);
        clip.centerXProperty().bind(logo.layoutBoundsProperty().map(Bounds::getCenterX));
        clip.centerYProperty().bind(logo.layoutBoundsProperty().map(Bounds::getCenterY));
        logo.setClip(clip);
        logoContainer.getChildren().add(logo);
    }

    private ToggleButton createToggle(String icon, String text) {
        MFXIconWrapper wrapper = new MFXIconWrapper(icon, 24, 32);
        MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode(text, wrapper);
        toggleNode.setAlignment(Pos.CENTER_LEFT);
        toggleNode.setMaxWidth(Double.MAX_VALUE);
        toggleNode.setToggleGroup(toggleGroup);
        return toggleNode;
    }

    public void cleanup() {
        executorService.shutdown();
        viewCache.clear();
    }
    private URL loadURL(String path) {
        URL url = getClass().getResource(path);
        if (url == null) {
            throw new IllegalArgumentException("FXML file not found: " + path);
        }
        return url;
    }
}


