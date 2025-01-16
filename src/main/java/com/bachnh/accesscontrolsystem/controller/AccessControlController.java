package com.bachnh.accesscontrolsystem.controller;
import com.bachnh.accesscontrolsystem.dto.AccessControlDTO;
import com.bachnh.accesscontrolsystem.model.AccessControlModel;
import com.bachnh.accesscontrolsystem.repository.AccessControlRepository;
import com.bachnh.accesscontrolsystem.repository.CommonAdapter;
import com.bachnh.accesscontrolsystem.utils.TableUtils;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPagination;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.DateTimeUtils;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
@Component
public class AccessControlController implements Initializable {
    @FXML
    private TableView<AccessControlDTO> fixedFirstTable;
    @FXML
    private TableView<AccessControlDTO> scrollableTable;
    @FXML
    private TableView<AccessControlDTO> fixedLastTable;
    @FXML
    private HBox tableContainer;
    @FXML
    private BorderPane borderPane;
    @FXML
    private MFXButton scanQrBtn;
    @FXML
    private MFXPagination paginator;
    @FXML
    private FXMLLoader loader;
    @FXML
    private MFXTextField txtCode;
    @FXML
    private MFXDatePicker dpFromDate;
    @FXML
    private MFXDatePicker dpToDate;
    @FXML
    private MFXButton btnSearch;
    private ObservableList<AccessControlDTO> masterData; // Danh sách dữ liệu gốc
    private final int ROWS_PER_PAGE = 30;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    @Autowired private AccessControlRepository accessControlRepository;
    @Autowired private ApplicationContext applicationContext;
    @Autowired private CommonAdapter commonAdapter;
    private Stage scanQrStage = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dpFromDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", dpFromDate.getLocale()));
        dpFromDate.setGridAlgorithm(DateTimeUtils::partialIntMonthMatrix);
        dpFromDate.setYearsRange(NumberRange.of(2025));
        dpToDate.setConverterSupplier(() -> new DateStringConverter("dd/MM/yyyy", dpToDate.getLocale()));
        dpToDate.setGridAlgorithm(DateTimeUtils::partialIntMonthMatrix);
        dpToDate.setYearsRange(NumberRange.of(2025));
        if (dpFromDate.getValue() == null) {
            dpFromDate.setValue(LocalDate.now());
        }

        if (dpToDate.getValue() == null) {
            dpToDate.setValue(LocalDate.now());
        }
        initializeData();
        setupPaginated();
        Platform.runLater(() -> {
            TableUtils.syncScrollBars(fixedFirstTable, scrollableTable, fixedLastTable);
            TableUtils.synchronizeTableSelection(fixedFirstTable, scrollableTable, fixedLastTable);
        });
        scanQrBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> openScanQR());
        btnSearch.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            initializeData();
            setupPaginated();
        });

    }

    public void initializeData() {
        if (accessControlRepository == null) {
            return;
        }
        String code = txtCode.getText();
        String fromDate = dpFromDate.getText();
        String toDate = dpToDate.getText();
        List<AccessControlModel> accessControls = commonAdapter.getListAccessControlSystem(code,fromDate,toDate);
        AtomicInteger count = new AtomicInteger(1);
        List<AccessControlDTO> data = accessControls.stream()
                .map(accessControl -> new AccessControlDTO(
                        count.getAndIncrement() ,
                        accessControl.getCode() != null ? accessControl.getCode() : null,
                        accessControl.getFullname() != null ? accessControl.getFullname() : null,
                        accessControl.getGender() != null ? accessControl.getGender():null,
                        accessControl.getDepartmentName()!= null ? accessControl.getDepartmentName():null,
                        accessControl.getRoleName()!= null ? accessControl.getRoleName():null,
                        accessControl.getType()!= null ? accessControl.getType():null,
                        accessControl.getStatus()!= null ? accessControl.getStatus():null,
                        accessControl.getCheckIn()!= null ? accessControl.getCheckIn():null,
                        accessControl.getCheckOut()!= null ? accessControl.getCheckOut():null

                ))
                .toList();
        masterData = FXCollections.observableArrayList(data);
        setupTable(masterData); // Khởi tạo bảng

    }

    public void setupPaginated() {
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
        // Nếu masterData trống, tạo danh sách rỗng
        if (masterData == null || masterData.isEmpty()) {
            fixedFirstTable.getItems().clear();
            scrollableTable.getItems().clear();
            fixedLastTable.getItems().clear();
            return;
        }

        int fromIndex = (pageIndex - 1) * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, masterData.size());

        ObservableList<AccessControlDTO> pageData = FXCollections.observableArrayList(
                masterData.subList(fromIndex, toIndex)
        );

        fixedFirstTable.getItems().clear();
        scrollableTable.getItems().clear();
        fixedLastTable.getItems().clear();

        Platform.runLater(() -> {
            fixedFirstTable.setItems(pageData);
            scrollableTable.setItems(pageData);
            fixedLastTable.setItems(pageData);
        });
    }

    private void setupTable(ObservableList<AccessControlDTO> data) {
        // Khởi tạo các cột bảng
        if (fixedFirstTable.getColumns().isEmpty()) {
            fixedFirstTable.setMinWidth(210);
            TableColumn<AccessControlDTO, String> IDColumn = new TableColumn<>("ID");
            TableColumn<AccessControlDTO, String> codeColumn = new TableColumn<>("Mã Nhân viên");
            IDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID())));
            codeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCode()));
            codeColumn.setMinWidth(150);
            fixedFirstTable.getColumns().addAll(IDColumn, codeColumn);
            fixedFirstTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            TableUtils.disableSorting(fixedFirstTable);
        }

        if (scrollableTable.getColumns().isEmpty()) {
            TableColumn<AccessControlDTO, String> fullnameColumn = new TableColumn<>("Họ và Tên");
            TableColumn<AccessControlDTO, String> genderColumn = new TableColumn<>("Giới tính");
            TableColumn<AccessControlDTO, String> departmentNameColumn = new TableColumn<>("Phòng ban");
            TableColumn<AccessControlDTO, String> roleNameColumn = new TableColumn<>("Chức vụ");
            TableColumn<AccessControlDTO, String> typeColumn = new TableColumn<>("Loại");
            TableColumn<AccessControlDTO, String> statusColumn = new TableColumn<>("Trạng thái");
            TableColumn<AccessControlDTO, String> checkInColumn = new TableColumn<>("Thời gian vào");
            TableColumn<AccessControlDTO, String> checkOutColumn = new TableColumn<>("Thời gian ra");

            fullnameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
            genderColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender()));
            departmentNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartmentName()));
            roleNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoleName()));
            typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
            statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
            checkInColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCheckIn() != null ? cellData.getValue().getCheckIn().format(DATETIME_FORMATTER): ""));
            checkOutColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCheckOut() != null ? cellData.getValue().getCheckOut().format(DATETIME_FORMATTER): ""));

            fullnameColumn.setMinWidth(200);
            genderColumn.setMinWidth(100);
            departmentNameColumn.setMinWidth(150);
            roleNameColumn.setMinWidth(150);
            typeColumn.setMinWidth(200);
            statusColumn.setMinWidth(100);

            scrollableTable.getColumns().addAll(fullnameColumn, genderColumn,departmentNameColumn, roleNameColumn,typeColumn, statusColumn);
            scrollableTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            TableUtils.disableSorting(scrollableTable);
        }

        if (fixedLastTable.getColumns().isEmpty()) {
            fixedLastTable.setMinWidth(300);
            TableColumn<AccessControlDTO, String> checkInColumn = new TableColumn<>("Thời gian vào");
            TableColumn<AccessControlDTO, String> checkOutColumn = new TableColumn<>("Thời gian ra");
            checkInColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCheckIn() != null ? cellData.getValue().getCheckIn().format(DATETIME_FORMATTER): ""));
            checkOutColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCheckOut() != null ? cellData.getValue().getCheckOut().format(DATETIME_FORMATTER): ""));
            checkInColumn.setMinWidth(150);
            checkOutColumn.setMinWidth(150);

            fixedLastTable.getColumns().addAll(checkInColumn,
                    checkOutColumn);
            fixedLastTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // Hiển thị khi bảng không có dữ liệu
            Label label = new Label("");
            fixedFirstTable.setPlaceholder(label);
            fixedLastTable.setPlaceholder(label);
            Label placeholderLabel = new Label("Không có dữ liệu");
            placeholderLabel.setStyle("-fx-text-fill: black;-fx-font-size: 16px");
            scrollableTable.setPlaceholder(placeholderLabel);
            TableUtils.disableSorting(fixedLastTable);
        }

    }
    private void openScanQR() {
        // Kiểm tra nếu cửa sổ đã mở thì không mở lại
        if (scanQrStage != null && scanQrStage.isShowing()) {
            scanQrStage.requestFocus(); // Focus vào cửa sổ đã mở
            return;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ScanQR.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        try {
            loader.load();
            Parent parent = loader.getRoot();
            scanQrStage = new Stage();
            scanQrStage.setScene(new Scene(parent));
            scanQrStage.initStyle(StageStyle.UTILITY);


            scanQrStage.setOnHidden(event -> {
                scanQrStage = null;  // Reset biến stage
                Platform.runLater(() -> {
                    initializeData();
                    setupPaginated();
                });
            });

            scanQrStage.show();
        } catch (IOException ex) {
            Logger.getLogger(AddEmployeeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

