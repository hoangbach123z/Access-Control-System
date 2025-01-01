package com.bachnh.accesscontrolsystem.controller;
import com.bachnh.accesscontrolsystem.dto.EmployeeDT0;
import com.bachnh.accesscontrolsystem.entity.Employee;

import com.bachnh.accesscontrolsystem.repository.EmployeeRepository;
import com.bachnh.accesscontrolsystem.utils.TableUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPagination;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Lazy
public class EmployeesController implements Initializable {
    @FXML
    private TableView<EmployeeDT0> fixedFirstTable;
    @FXML
    private TableView<EmployeeDT0> scrollableTable;
    @FXML
    private TableView<EmployeeDT0> fixedLastTable;
    @FXML
    private HBox tableContainer;
    @FXML
    private BorderPane borderPane;
    @FXML
    private MFXButton addEmployeeBtn;
    @FXML
    private MFXPagination paginator;

    private FXMLLoader loader;
    private ObservableList<EmployeeDT0> masterData; // Danh sách dữ liệu gốc
    private final int ROWS_PER_PAGE = 30;
    private final Map<Integer, ObservableList<EmployeeDT0>> pageCache = new HashMap<>();
    private static final Map<String, Parent> fxmlCache = new HashMap<>();
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Task<Void> initTask = new Task<>() {
            @Override
            protected Void call() {
                initializeData();
                addEmployee();
                return null;
            }
        };

        initTask.setOnSucceeded(e -> {
            setupPaginated();
            Platform.runLater(() -> {
                TableUtils.syncScrollBars(fixedFirstTable, scrollableTable, fixedLastTable);
                TableUtils.synchronizeTableSelection(fixedFirstTable, scrollableTable, fixedLastTable);
            });
//            addEmployee();
        });

        new Thread(initTask).start();
    }

    private void initializeData() {
        if (employeeRepository ==null){
            return ;
        }
        Task<ObservableList<EmployeeDT0>> loadDataTask = new Task<>() {
             List<Employee> employees = employeeRepository.findAll();
             AtomicInteger count = new AtomicInteger(1);
            @Override
            protected ObservableList<EmployeeDT0> call() {
                return FXCollections.observableArrayList(
                        employees.stream()
                                .map(employee -> new EmployeeDT0(
                                        count.getAndIncrement() ,
                                        employee.getEmployeecode() != null ? employee.getEmployeecode() : null,
                                        employee.getFullname() != null ? employee.getFullname() : null,
                                        employee.getGender() != null ? employee.getGender() : null,
                                        employee.getBirthday() != null ? employee.getBirthday() : null,
                                        employee.getCardId() != null ? employee.getCardId() : null,
                                        employee.getMobile() != null ? employee.getMobile() : null,
                                        employee.getEmail() != null ? employee.getEmail() : null,
                                        employee.getAddress() != null ? employee.getAddress() : null,
                                        employee.getStatus() != null ? employee.getStatus() : null,
                                        employee.getCreateDate() != null ? employee.getCreateDate() : null,
                                        employee.getUpdateDate() != null ? employee.getUpdateDate() : null))
                                .toList()
                );
            }

        };

        loadDataTask.setOnSucceeded(e -> {
            masterData = loadDataTask.getValue();
            if (masterData != null) {
                Platform.runLater(() -> {
                    setupTable(masterData);
                    setupPaginated();
                    TableUtils.syncScrollBars(fixedFirstTable, scrollableTable, fixedLastTable);
                    TableUtils.synchronizeTableSelection(fixedFirstTable, scrollableTable, fixedLastTable);
//                    addEmployee();
                    // Xóa loading indicator
                    borderPane.setCenter(tableContainer);
                });
            }
        });

        loadDataTask.setOnFailed(e -> {
            Throwable exception = loadDataTask.getException();
            Platform.runLater(() -> {
                Label errorLabel = new Label("Không thể tải dữ liệu. Vui lòng thử lại sau.");
                errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
                borderPane.setCenter(errorLabel);
                if (exception != null) {
                    exception.printStackTrace();
                }
            });
        });

        Thread thread = new Thread(loadDataTask);
        thread.setDaemon(true); // Đảm bảo thread sẽ dừng khi ứng dụng đóng
        thread.start();
    }

    private void setupPaginated() {
        // Kiểm tra nếu masterData không tồn tại hoặc không có dữ liệu
        if (masterData == null || masterData.isEmpty()) {
            paginator.setMaxPage(1); // Đặt số trang tối thiểu là 1
            paginator.setCurrentPage(1);
            updateTableData(1); // Đảm bảo bảng hiển thị dữ liệu rỗng
            return;
        }

        int totalPages = (int) Math.ceil((double) masterData.size() / ROWS_PER_PAGE);
        paginator.setMaxPage(totalPages);
        paginator.setCurrentPage(1);

        // Xử lý sự kiện khi chuyển trang
        paginator.currentPageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateTableData(newValue.intValue());
            }
        });

        // Hiển thị dữ liệu trang đầu tiên
        updateTableData(1);
    }

    private void updateTableData(int pageIndex) {
        Task<ObservableList<EmployeeDT0>> updateTask = new Task<>() {
            @Override
            protected ObservableList<EmployeeDT0> call() {
                if (masterData == null || masterData.isEmpty()) {
                    return FXCollections.observableArrayList();
                }
                int fromIndex = (pageIndex - 1) * ROWS_PER_PAGE;
                int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, masterData.size());
                return FXCollections.observableArrayList(masterData.subList(fromIndex, toIndex));
            }
        };

        updateTask.setOnSucceeded(e -> {
            ObservableList<EmployeeDT0> pageData = updateTask.getValue();
            Platform.runLater(() -> {
                fixedFirstTable.getItems().clear();
                scrollableTable.getItems().clear();
                fixedLastTable.getItems().clear();

                fixedFirstTable.setItems(pageData);
                scrollableTable.setItems(pageData);
                fixedLastTable.setItems(pageData);
            });
        });

        new Thread(updateTask).start();
    }

    private void setupTable(ObservableList<EmployeeDT0> data) {
        Task<Void> setupTask = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    if (fixedFirstTable.getColumns().isEmpty()) {
                        fixedFirstTable.setMinWidth(210);
                        TableColumn<EmployeeDT0, String> IDColumn = new TableColumn<>("ID");
                        TableColumn<EmployeeDT0, String> employeeCodeColumn = new TableColumn<>("Mã Nhân viên");
                        IDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID())));
                        employeeCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmployeeCode()));
                        employeeCodeColumn.setMinWidth(150);
                        fixedFirstTable.getColumns().addAll(IDColumn, employeeCodeColumn);
                        fixedFirstTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                        TableUtils.disableSorting(fixedFirstTable);
                    }

                    if (scrollableTable.getColumns().isEmpty()) {
                        TableColumn<EmployeeDT0, String> fullnameColumn = new TableColumn<>("Họ và Tên");
                        TableColumn<EmployeeDT0, String> genderColumn = new TableColumn<>("Giới tính");
                        TableColumn<EmployeeDT0, String> birthdayColumn = new TableColumn<>("Ngày sinh");
                        TableColumn<EmployeeDT0, String> mobileColumn = new TableColumn<>("Số điện thoại");
                        TableColumn<EmployeeDT0, String> cardIdColumn = new TableColumn<>("CMND/CCCD");
                        TableColumn<EmployeeDT0, String> emailColumn = new TableColumn<>("Email");
                        TableColumn<EmployeeDT0, String> addressColumn = new TableColumn<>("Địa chỉ");
                        TableColumn<EmployeeDT0, String> departmentNameColumn = new TableColumn<>("Phòng ban");
                        TableColumn<EmployeeDT0, String> roleNameColumn = new TableColumn<>("Vị trí");
                        TableColumn<EmployeeDT0, String> statusColumn = new TableColumn<>("Trạng thái");
                        TableColumn<EmployeeDT0, String> createDateColumn = new TableColumn<>("Ngày tạo");
                        TableColumn<EmployeeDT0, String> updateDateColumn = new TableColumn<>("Ngày cập nhật");

                        fullnameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullname()));
                        genderColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender()));
                        DateTimeFormatter formatterBirthday = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        birthdayColumn.setCellValueFactory(cellData ->
                                new SimpleStringProperty(
                                        cellData.getValue().getBirthday() != null
                                                ? cellData.getValue().getBirthday().format(formatterBirthday)
                                                : ""
                                )
                        );
                        mobileColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMobile()));
                        cardIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCardID()));
                        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
                        addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
                        departmentNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartmentName()));
                        roleNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoleName()));
                        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        createDateColumn.setCellValueFactory(cellData ->
                                new SimpleStringProperty(
                                        cellData.getValue().getCreateDate() != null
                                                ? cellData.getValue().getCreateDate().format(formatter)
                                                : ""
                                )
                        );

                        updateDateColumn.setCellValueFactory(cellData ->
                                new SimpleStringProperty(
                                        cellData.getValue().getUpdateDate() != null
                                                ? cellData.getValue().getUpdateDate().format(formatter)
                                                : ""
                                )
                        );

                        fullnameColumn.setMinWidth(200);
                        genderColumn.setMinWidth(100);
                        birthdayColumn.setMinWidth(150);
                        mobileColumn.setMinWidth(150);
                        cardIdColumn.setMinWidth(150);
                        emailColumn.setMinWidth(200);
                        addressColumn.setMinWidth(200);
                        departmentNameColumn.setMinWidth(150);
                        roleNameColumn.setMinWidth(150);
                        statusColumn.setMinWidth(100);
                        createDateColumn.setMinWidth(150);
                        updateDateColumn.setMinWidth(150);
                        scrollableTable.getColumns().addAll(fullnameColumn, genderColumn, birthdayColumn, mobileColumn, cardIdColumn,
                                emailColumn, addressColumn, departmentNameColumn, roleNameColumn, statusColumn, createDateColumn,
                                updateDateColumn);
                        scrollableTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
                        TableUtils.disableSorting(scrollableTable);
                    }

                    if (fixedLastTable.getColumns().isEmpty()) {
                        TableColumn<EmployeeDT0, Void> actionColumn = new TableColumn<>("Hành động");

                        // Tạo Callback để sử dụng cho setCellFactory
                        Callback<TableColumn<EmployeeDT0, Void>, TableCell<EmployeeDT0, Void>> cellFactory = (TableColumn<EmployeeDT0, Void> param) -> {
                            return new TableCell<>() {
                                private final HBox actionBox = new HBox(10);
                                {
                                    actionBox.setAlignment(Pos.CENTER);
                                    // Icon xem chi tiết
                                    MFXFontIcon viewIcon = new MFXFontIcon("fas-eye", 18);
                                    viewIcon.setStyle("-fx-cursor: hand;");
                                    viewIcon.setColor(Color.FORESTGREEN);
                                    viewIcon.setOnMouseClicked(event -> loadSceneAsync("/fxml/EmployeeDetail.fxml"));

                                    // Icon chỉnh sửa
                                    MFXFontIcon editIcon = new MFXFontIcon("fas-pen-to-square", 18);
                                    editIcon.setStyle("-fx-cursor: hand;");
                                    editIcon.setColor(Color.BLUE);
                                    editIcon.setOnMouseClicked(event -> loadSceneAsync("/fxml/EditEmployee.fxml"));

                                    // Icon xóa
                                    MFXFontIcon deleteIcon = new MFXFontIcon("fas-trash-can", 18);
                                    deleteIcon.setStyle("-fx-cursor: hand;");
                                    deleteIcon.setColor(Color.RED);
                                    deleteIcon.setOnMouseClicked(event -> {
                                        Stage currentStage = (Stage) borderPane.getScene().getWindow();
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.initOwner(currentStage);
                                        alert.initModality(Modality.WINDOW_MODAL);
                                        alert.setTitle("Xác nhận xóa");
                                        alert.setHeaderText("Bạn có chắc chắn muốn xóa người dùng này?");
                                        alert.setContentText("Hành động này không thể hoàn tác.");

                                        alert.showAndWait().ifPresent(response -> {
                                            if (response == ButtonType.OK) {
                                                System.out.println("Xóa nhân viên: " + getTableView().getItems().get(getIndex()).getFullname());
                                            }
                                        });
                                    });

                                    // Thêm các icon vào HBox
                                    actionBox.getChildren().addAll(viewIcon, editIcon, deleteIcon);
                                }

                                @Override
                                protected void updateItem(Void item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty) {
                                        setGraphic(null); // Không hiển thị nếu cell rỗng
                                    } else {
                                        setGraphic(actionBox); // Hiển thị actionBox
                                    }
                                }
                            };
                        };

                        actionColumn.setCellFactory(cellFactory);
                        fixedLastTable.getColumns().add(actionColumn);
                        fixedLastTable.setMinWidth(120);
                        fixedLastTable.setMaxWidth(120);
                        fixedLastTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                        TableUtils.disableSorting(fixedLastTable);
                    }
                });
                return null;
            }
        };

        setupTask.setOnSucceeded(e -> Platform.runLater(() -> {
            Label label = new Label("");
            fixedFirstTable.setPlaceholder(label);
            fixedLastTable.setPlaceholder(label);
            Label placeholderLabel = new Label("Không có dữ liệu");
            placeholderLabel.setStyle("-fx-text-fill: black;-fx-font-size: 16px");
            scrollableTable.setPlaceholder(placeholderLabel);
        }));

        new Thread(setupTask).start();
    }
    private void addEmployee() {
        addEmployeeBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                loadSceneAsync("/fxml/AddEmployee.fxml"));
    }
    public class FXMLCache {
        private static final Map<String, FXMLLoader> cache = new HashMap<>();

        public static Parent get(String fxmlPath) throws IOException {
            if (cache.containsKey(fxmlPath)) {
                // Tạo FXMLLoader mới nhưng sử dụng controller đã cache
                FXMLLoader newLoader = new FXMLLoader(cache.get(fxmlPath).getLocation());
                return newLoader.load();
            }
            return null;
        }

        public static void put(String fxmlPath, FXMLLoader loader) {
            cache.put(fxmlPath, loader);
        }

        public static boolean contains(String fxmlPath) {
            return cache.containsKey(fxmlPath);
        }

        public static void clear() {
            cache.clear();
        }
    }

    private void loadSceneAsync(String fxmlPath) {
        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws IOException {
                Parent parent;
                if (FXMLCache.contains(fxmlPath)) {
                    parent = FXMLCache.get(fxmlPath);
                } else {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource(fxmlPath));
                    loader.setControllerFactory(applicationContext::getBean);
                    parent = loader.load();
                    FXMLCache.put(fxmlPath, loader);
                }
                return parent;
            }
        };

        loadTask.setOnSucceeded(e -> {
            Parent parent = loadTask.getValue();
            Platform.runLater(() -> {
                Stage stage = new Stage();
                Scene scene = new Scene(parent);
                stage.setScene(scene);
                stage.initStyle(StageStyle.UTILITY);
                stage.show();
            });
        });

        loadTask.setOnFailed(e -> {
            Logger.getLogger(getClass().getName()).log(
                    Level.SEVERE,
                    "Error loading FXML",
                    loadTask.getException()
            );
        });

        new Thread(loadTask).start();
    }
    public void cleanup() {
        fxmlCache.clear();
    }

}