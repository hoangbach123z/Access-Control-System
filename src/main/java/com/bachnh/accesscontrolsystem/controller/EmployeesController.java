package com.bachnh.accesscontrolsystem.controller;
import com.bachnh.accesscontrolsystem.dto.EmployeeDT0;

import com.bachnh.accesscontrolsystem.entity.Employee;
import com.bachnh.accesscontrolsystem.model.EmployeeModel;
import com.bachnh.accesscontrolsystem.repository.CommonAdapter;
import com.bachnh.accesscontrolsystem.repository.EmployeeRepository;
import com.bachnh.accesscontrolsystem.utils.TableUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPagination;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Lazy
public class EmployeesController implements Initializable {
    private static final int ROWS_PER_PAGE = 30;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML private TableView<EmployeeDT0> fixedFirstTable;
    @FXML private TableView<EmployeeDT0> scrollableTable;
    @FXML private TableView<EmployeeDT0> fixedLastTable;
    @FXML private HBox tableContainer;
    @FXML private BorderPane borderPane;
    @FXML private MFXButton addEmployeeBtn;
    @FXML private MFXPagination paginator;
    private FXMLLoader loader;
    private ObservableList<EmployeeDT0> masterData;

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private CommonAdapter commonAdapter;
    @Autowired private ApplicationContext applicationContext;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeData();
        setupPaginated();
        Platform.runLater(this::syncTables);
//        addEmployeeBtn.setOnAction(event -> handleAddAction());
        addEmployeeBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> addEmployee());;

    }

    private void syncTables() {
        TableUtils.syncScrollBars(fixedFirstTable, scrollableTable, fixedLastTable);
        TableUtils.synchronizeTableSelection(fixedFirstTable, scrollableTable, fixedLastTable);
    }

    private void initializeData() {
        if (commonAdapter == null) return;

        List<EmployeeModel> employees = commonAdapter.getListEmployees();
        AtomicInteger count = new AtomicInteger(1);

        List<EmployeeDT0> data = employees.stream()
                .map(employee -> new EmployeeDT0(
                        count.getAndIncrement(),
                        (employee.getEmployeeCode()),
                        (employee.getFullname()),
                        (employee.getGender()),
                        employee.getBirthday(),
                        (employee.getCardID()),
                        (employee.getMobile()),
                        (employee.getEmail()),
                        (employee.getAddress()),
                        (employee.getDepartmentName()),
                        (employee.getRoleName()),
                        (employee.getStatus()),
                        employee.getCreateDate(),
                        employee.getUpdateDate()
                ))
                .toList();

        masterData = FXCollections.observableArrayList(data);
        setupTable(masterData);
    }
    
    private void setupPaginated() {
        if (masterData == null || masterData.isEmpty()) {
            paginator.setMaxPage(1);
            paginator.setCurrentPage(1);
            updateTableData(1);
            return;
        }

        int totalPages = (int) Math.ceil((double) masterData.size() / ROWS_PER_PAGE);
        paginator.setMaxPage(totalPages);
        paginator.setCurrentPage(1);

        paginator.currentPageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateTableData(newValue.intValue());
            }
        });

        updateTableData(1);
    }

    private void updateTableData(int pageIndex) {
        if (masterData == null || masterData.isEmpty()) {
            clearTables();
            return;
        }

        int fromIndex = (pageIndex - 1) * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, masterData.size());

        ObservableList<EmployeeDT0> pageData = FXCollections.observableArrayList(
                masterData.subList(fromIndex, toIndex)
        );

        Platform.runLater(() -> {
            clearTables();
            setTableData(pageData);
        });
    }

    private void clearTables() {
        fixedFirstTable.getItems().clear();
        scrollableTable.getItems().clear();
        fixedLastTable.getItems().clear();
    }

    private void setTableData(ObservableList<EmployeeDT0> data) {
        fixedFirstTable.setItems(data);
        scrollableTable.setItems(data);
        fixedLastTable.setItems(data);
    }

    private void setupTable(ObservableList<EmployeeDT0> data) {
        Platform.runLater(() -> {
            setupFixedFirstTable();
            setupScrollableTable();
            setupFixedLastTable();
            setupTablePlaceholders();
        });
    }
    private void setupFixedFirstTable() {
        if (!fixedFirstTable.getColumns().isEmpty()) return;

        fixedFirstTable.setMinWidth(210);

        TableColumn<EmployeeDT0, String> idColumn = createColumn("ID",
                employee -> new SimpleStringProperty(String.valueOf(employee.getID())));
        TableColumn<EmployeeDT0, String> codeColumn = createColumn("Mã Nhân viên",
                employee -> new SimpleStringProperty(employee.getEmployeeCode()));

        codeColumn.setMinWidth(150);
        fixedFirstTable.getColumns().addAll(idColumn, codeColumn);
        fixedFirstTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableUtils.disableSorting(fixedFirstTable);
    }

    private void setupScrollableTable() {
        if (!scrollableTable.getColumns().isEmpty()) return;

        List<TableColumn<EmployeeDT0, String>> columns = new ArrayList<>();

        columns.add(createColumnWithWidth("Họ và Tên", e -> new SimpleStringProperty(e.getFullname()), 200));
        columns.add(createColumnWithWidth("Giới tính", e -> new SimpleStringProperty(e.getGender()), 100));
        columns.add(createColumnWithWidth("Ngày sinh", e -> new SimpleStringProperty(
                e.getBirthday() != null ? e.getBirthday().format(DATE_FORMATTER) : ""), 150));
        columns.add(createColumnWithWidth("Số điện thoại", e -> new SimpleStringProperty(e.getMobile()), 150));
        columns.add(createColumnWithWidth("CMND/CCCD", e -> new SimpleStringProperty(e.getCardID()), 150));
        columns.add(createColumnWithWidth("Email", e -> new SimpleStringProperty(e.getEmail()), 200));
        columns.add(createColumnWithWidth("Địa chỉ", e -> new SimpleStringProperty(e.getAddress()), 200));
        columns.add(createColumnWithWidth("Phòng ban", e -> new SimpleStringProperty(e.getDepartmentName()), 150));
        columns.add(createColumnWithWidth("Chức vụ", e -> new SimpleStringProperty(e.getRoleName()), 150));
        columns.add(createColumnWithWidth("Trạng thái", e -> new SimpleStringProperty(e.getStatus()), 100));
        columns.add(createColumnWithWidth("Ngày tạo", e -> new SimpleStringProperty(
                e.getCreateDate() != null ? e.getCreateDate().format(DATETIME_FORMATTER) : ""), 150));
        columns.add(createColumnWithWidth("Ngày cập nhật", e -> new SimpleStringProperty(
                e.getUpdateDate() != null ? e.getUpdateDate().format(DATETIME_FORMATTER) : ""), 150));

        scrollableTable.getColumns().addAll(columns);
        scrollableTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        TableUtils.disableSorting(scrollableTable);
    }

    private void setupFixedLastTable() {
        if (!fixedLastTable.getColumns().isEmpty()) return;

        TableColumn<EmployeeDT0, Void> actionColumn = new TableColumn<>("Hành động");
        actionColumn.setCellFactory(column -> new ActionTableCell());

        fixedLastTable.getColumns().add(actionColumn);
        fixedLastTable.setMinWidth(120);
        fixedLastTable.setMaxWidth(120);
        fixedLastTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableUtils.disableSorting(fixedLastTable);
    }

    private class ActionTableCell extends TableCell<EmployeeDT0, Void> {
        private final HBox actionBox;

        public ActionTableCell() {
            actionBox = new HBox(10);
            actionBox.setAlignment(Pos.CENTER);
            setupActionIcons();
        }

        private void setupActionIcons() {
            MFXFontIcon viewIcon = createActionIcon("fas-eye", Color.FORESTGREEN, this::handleViewAction);
            MFXFontIcon editIcon = createActionIcon("fas-pen-to-square", Color.BLUE, this::handleEditAction);
            MFXFontIcon deleteIcon = createActionIcon("fas-trash-can", Color.RED, this::handleDeleteAction);

            actionBox.getChildren().addAll(viewIcon, editIcon, deleteIcon);
        }

        private MFXFontIcon createActionIcon(String iconCode, Color color, EventHandler<MouseEvent> handler) {
            MFXFontIcon icon = new MFXFontIcon(iconCode, 18);
            icon.setStyle("-fx-cursor: hand;");
            icon.setColor(color);
            icon.setOnMouseClicked(handler);
            return icon;
        }

        private void handleViewAction(MouseEvent event) {
            openDialog("/fxml/EmployeeDetail.fxml");
            EmployeeDT0 employee = getTableView().getItems().get(getIndex());
            Employee employeeCode = employeeRepository.findByEmployeecode(employee.getEmployeeCode());
            EmployeeDetailController employeeDetailController = loader.getController();
            employeeDetailController.handleView(employeeCode.getEmployeecode());
        }

        private void handleEditAction(MouseEvent event) {
            openDialog("/fxml/EditEmployee.fxml");
        }


        private void handleDeleteAction(MouseEvent event) {
            EmployeeDT0 employee = getTableView().getItems().get(getIndex());
            showDeleteConfirmation(employee);
        }

        private void openDialog(String fxmlPath) {
            try {
                loader = new FXMLLoader(getClass().getResource(fxmlPath));
                loader.setControllerFactory(applicationContext::getBean);
                Parent parent = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(parent));
                stage.initStyle(StageStyle.UTILITY);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(EditEmployeeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void showDeleteConfirmation(EmployeeDT0 employee) {
            Stage currentStage = (Stage) borderPane.getScene().getWindow();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(currentStage);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setTitle("Xác nhận xóa");
            alert.setHeaderText("Bạn có chắc chắn muốn xóa người dùng này?");
            alert.setContentText("Hành động này không thể hoàn tác.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    deleteEmployee(employee);
                }
            });
        }

        private void deleteEmployee(EmployeeDT0 employee) {
            Employee deleteByEmpCode = employeeRepository.findByEmployeecode(employee.getEmployeeCode());
            employeeRepository.delete(deleteByEmpCode);
            Platform.runLater(() -> {
                initializeData();
                setupPaginated();
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : actionBox);
        }
    }

    private TableColumn<EmployeeDT0, String> createColumn(String title,
                                                          Function<EmployeeDT0, ObservableValue<String>> valueFactory) {
        TableColumn<EmployeeDT0, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> valueFactory.apply(cellData.getValue()));
        return column;
    }

    private TableColumn<EmployeeDT0, String> createColumnWithWidth(String title,
                                                                   Function<EmployeeDT0, ObservableValue<String>> valueFactory, double width) {
        TableColumn<EmployeeDT0, String> column = createColumn(title, valueFactory);
        column.setMinWidth(width);
        return column;
    }

    private void setupTablePlaceholders() {
        Label emptyLabel = new Label("");
        Label noDataLabel = new Label("Không có dữ liệu");
        noDataLabel.setStyle("-fx-text-fill: black;-fx-font-size: 16px");

        fixedFirstTable.setPlaceholder(emptyLabel);
        fixedLastTable.setPlaceholder(emptyLabel);
        scrollableTable.setPlaceholder(noDataLabel);
    }

    private void addEmployee() {
        loader = new FXMLLoader(getClass().getResource("/fxml/AddEmployee.fxml"));
        loader.setControllerFactory(applicationContext::getBean);

        try {
            Parent parent = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.initStyle(StageStyle.UTILITY);
            stage.show();

            stage.setOnHidden(event -> {
                initializeData();
                setupPaginated();
            });
        } catch (IOException ex) {
            Logger.getLogger(AddEmployeeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}