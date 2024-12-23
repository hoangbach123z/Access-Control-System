package com.bachnh.accesscontrolsystem.controller;

import com.bachnh.accesscontrolsystem.dto.EmployeeDT0;
import com.bachnh.accesscontrolsystem.model.Device;
import com.bachnh.accesscontrolsystem.model.Model;
import com.bachnh.accesscontrolsystem.utils.TableUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXPagination;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.materialfx.filter.EnumFilter;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.materialfx.utils.others.observables.When;
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

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RolesController implements Initializable {
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
    @FXML
//    private ScrollPane scrollPane;
    private FXMLLoader loader;
    private ObservableList<EmployeeDT0> masterData; // Danh sách dữ liệu gốc
    private final int ROWS_PER_PAGE = 30;
    private final Map<Integer, ObservableList<EmployeeDT0>> pageCache = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeData();
        setupPaginated();
        Platform.runLater(() -> {
            TableUtils.syncScrollBars(fixedFirstTable, scrollableTable, fixedLastTable);
            TableUtils.synchronizeTableSelection(fixedFirstTable, scrollableTable, fixedLastTable);
        });
    }

    private void initializeData() {
        masterData = FXCollections.observableArrayList(
                new EmployeeDT0("1", "EMP001", "Nguyễn Văn A", "Nam", "01/01/1990", "0912345678", "123456789", "adsadsadsadsadsa@gmail.com", "Hà Nội", "Phòng IT", "Nhân viên", "Hoạt động", "01/01/2023", "02/01/2023"),
                new EmployeeDT0("2", "EMP002", "Trần Thị B", "Nữ", "15/02/1992", "0918765432", "987654321", "bdsadsadsadsadsa@gmail.com", "Hồ Chí Minh", "Phòng HR", "Quản lý", "Hoạt động", "05/01/2023", "06/01/2023"),
                new EmployeeDT0("3", "EMP003", "Lê Văn C", "Nam", "20/03/1985", "0987654321", "111222333", "cđasadsadsad@gmail.com", "Đà Nẵng", "Phòng Kế Toán", "Kế toán trưởng", "Hoạt động", "10/01/2023", "11/01/2023"),
                new EmployeeDT0("4", "EMP004", "Phạm Thị D", "Nữ", "10/04/1995", "0971122334", "444555666", "ddsadsadsadsa@gmail.com", "Hải Phòng", "Phòng IT", "Nhân viên", "Nghỉ việc", "12/01/2023", "14/01/2023"),
                new EmployeeDT0("5", "EMP005", "Ngô Văn E", "Nam", "05/05/1993", "0933344455", "777888999", "edsadsadsads@gmail.com", "Cần Thơ", "Phòng Sales", "Trưởng phòng", "Hoạt động", "15/01/2023", "16/01/2023"),
                new EmployeeDT0("6", "EMP006", "Hoàng Thị F", "Nữ", "25/06/1991", "0956677889", "000111222", "fdsadsadsadsa@gmail.com", "Hà Nội", "Phòng Marketing", "Nhân viên", "Hoạt động", "17/01/2023", "18/01/2023"),
                new EmployeeDT0("7", "EMP007", "Vũ Văn G", "Nam", "30/07/1988", "0911223344", "333222111", "gdsadsdsdsadsa@gmail.com", "Hồ Chí Minh", "Phòng IT", "Quản lý", "Hoạt động", "19/01/2023", "20/01/2023"),
                new EmployeeDT0("8", "EMP008", "Đặng Thị H", "Nữ", "15/08/1994", "0944455566", "666555444", "hdsadsadsadsad@gmail.com", "Hải Dương", "Phòng HR", "Nhân viên", "Nghỉ việc", "21/01/2023", "22/01/2023"),
                new EmployeeDT0("9", "EMP009", "Phan Văn I", "Nam", "12/09/1987", "0922233344", "999888777", "idsadsadsadsa@gmail.com", "Quảng Ninh", "Phòng Kỹ Thuật", "Trưởng phòng", "Hoạt động", "23/01/2023", "24/01/2023"),
                new EmployeeDT0("10", "EMP010", "Lý Thị J", "Nữ", "01/10/1990", "0915566778", "123321456", "jdsadsdsadsa@gmail.com", "Vĩnh Phúc", "Phòng IT", "Nhân viên", "Hoạt động", "25/01/2023", "26/01/2023"),
                new EmployeeDT0("11", "EMP001", "Nguyễn Văn A", "Nam", "01/01/1990", "0912345678", "123456789", "adsadsadsadsadsa@gmail.com", "Hà Nội", "Phòng IT", "Nhân viên", "Hoạt động", "01/01/2023", "02/01/2023"),
                new EmployeeDT0("12", "EMP002", "Trần Thị B", "Nữ", "15/02/1992", "0918765432", "987654321", "bdsadsadsadsadsa@gmail.com", "Hồ Chí Minh", "Phòng HR", "Quản lý", "Hoạt động", "05/01/2023", "06/01/2023"),
                new EmployeeDT0("13", "EMP003", "Lê Văn C", "Nam", "20/03/1985", "0987654321", "111222333", "cđasadsadsad@gmail.com", "Đà Nẵng", "Phòng Kế Toán", "Kế toán trưởng", "Hoạt động", "10/01/2023", "11/01/2023"),
                new EmployeeDT0("14", "EMP004", "Phạm Thị D", "Nữ", "10/04/1995", "0971122334", "444555666", "ddsadsadsadsa@gmail.com", "Hải Phòng", "Phòng IT", "Nhân viên", "Nghỉ việc", "12/01/2023", "14/01/2023"),
                new EmployeeDT0("15", "EMP005", "Ngô Văn E", "Nam", "05/05/1993", "0933344455", "777888999", "edsadsadsads@gmail.com", "Cần Thơ", "Phòng Sales", "Trưởng phòng", "Hoạt động", "15/01/2023", "16/01/2023"),
                new EmployeeDT0("16", "EMP006", "Hoàng Thị F", "Nữ", "25/06/1991", "0956677889", "000111222", "fdsadsadsadsa@gmail.com", "Hà Nội", "Phòng Marketing", "Nhân viên", "Hoạt động", "17/01/2023", "18/01/2023"),
                new EmployeeDT0("17", "EMP007", "Vũ Văn G", "Nam", "30/07/1988", "0911223344", "333222111", "gdsadsdsdsadsa@gmail.com", "Hồ Chí Minh", "Phòng IT", "Quản lý", "Hoạt động", "19/01/2023", "20/01/2023"),
                new EmployeeDT0("18", "EMP008", "Đặng Thị H", "Nữ", "15/08/1994", "0944455566", "666555444", "hdsadsadsadsad@gmail.com", "Hải Dương", "Phòng HR", "Nhân viên", "Nghỉ việc", "21/01/2023", "22/01/2023"),
                new EmployeeDT0("19", "EMP009", "Phan Văn I", "Nam", "12/09/1987", "0922233344", "999888777", "idsadsadsadsa@gmail.com", "Quảng Ninh", "Phòng Kỹ Thuật", "Trưởng phòng", "Hoạt động", "23/01/2023", "24/01/2023"),
                new EmployeeDT0("20", "EMP010", "Lý Thị J", "Nữ", "01/10/1990", "0915566778", "123321456", "jdsadsdsadsa@gmail.com", "Vĩnh Phúc", "Phòng IT", "Nhân viên", "Hoạt động", "25/01/2023", "26/01/2023"),
                new EmployeeDT0("21", "EMP001", "Nguyễn Văn A", "Nam", "01/01/1990", "0912345678", "123456789", "adsadsadsadsadsa@gmail.com", "Hà Nội", "Phòng IT", "Nhân viên", "Hoạt động", "01/01/2023", "02/01/2023"),
                new EmployeeDT0("22", "EMP002", "Trần Thị B", "Nữ", "15/02/1992", "0918765432", "987654321", "bdsadsadsadsadsa@gmail.com", "Hồ Chí Minh", "Phòng HR", "Quản lý", "Hoạt động", "05/01/2023", "06/01/2023"),
                new EmployeeDT0("23", "EMP003", "Lê Văn C", "Nam", "20/03/1985", "0987654321", "111222333", "cđasadsadsad@gmail.com", "Đà Nẵng", "Phòng Kế Toán", "Kế toán trưởng", "Hoạt động", "10/01/2023", "11/01/2023"),
                new EmployeeDT0("24", "EMP004", "Phạm Thị D", "Nữ", "10/04/1995", "0971122334", "444555666", "ddsadsadsadsa@gmail.com", "Hải Phòng", "Phòng IT", "Nhân viên", "Nghỉ việc", "12/01/2023", "14/01/2023"),
                new EmployeeDT0("25", "EMP005", "Ngô Văn E", "Nam", "05/05/1993", "0933344455", "777888999", "edsadsadsads@gmail.com", "Cần Thơ", "Phòng Sales", "Trưởng phòng", "Hoạt động", "15/01/2023", "16/01/2023"),
                new EmployeeDT0("26", "EMP006", "Hoàng Thị F", "Nữ", "25/06/1991", "0956677889", "000111222", "fdsadsadsadsa@gmail.com", "Hà Nội", "Phòng Marketing", "Nhân viên", "Hoạt động", "17/01/2023", "18/01/2023"),
                new EmployeeDT0("27", "EMP007", "Vũ Văn G", "Nam", "30/07/1988", "0911223344", "333222111", "gdsadsdsdsadsa@gmail.com", "Hồ Chí Minh", "Phòng IT", "Quản lý", "Hoạt động", "19/01/2023", "20/01/2023"),
                new EmployeeDT0("28", "EMP008", "Đặng Thị H", "Nữ", "15/08/1994", "0944455566", "666555444", "hdsadsadsadsad@gmail.com", "Hải Dương", "Phòng HR", "Nhân viên", "Nghỉ việc", "21/01/2023", "22/01/2023"),
                new EmployeeDT0("29", "EMP009", "Phan Văn I", "Nam", "12/09/1987", "0922233344", "999888777", "idsadsadsadsa@gmail.com", "Quảng Ninh", "Phòng Kỹ Thuật", "Trưởng phòng", "Hoạt động", "23/01/2023", "24/01/2023"),
                new EmployeeDT0("30", "EMP010", "Lý Thị J", "Nữ", "01/10/1990", "0915566778", "123321456", "jdsadsdsadsa@gmail.com", "Vĩnh Phúc", "Phòng IT", "Nhân viên", "Hoạt động", "25/01/2023", "26/01/2023"),
                new EmployeeDT0("31", "EMP010", "Lý Thị J", "Nữ", "01/10/1990", "0915566778", "123321456", "jdsadsdsadsa@gmail.com", "Vĩnh Phúc", "Phòng IT", "Nhân viên", "Hoạt động", "25/01/2023", "26/01/2023"),
                new EmployeeDT0("32", "EMP001", "Nguyễn Văn A", "Nam", "01/01/1990", "0912345678", "123456789", "adsadsadsadsadsa@gmail.com", "Hà Nội", "Phòng IT", "Nhân viên", "Hoạt động", "01/01/2023", "02/01/2023"),
                new EmployeeDT0("33", "EMP002", "Trần Thị B", "Nữ", "15/02/1992", "0918765432", "987654321", "bdsadsadsadsadsa@gmail.com", "Hồ Chí Minh", "Phòng HR", "Quản lý", "Hoạt động", "05/01/2023", "06/01/2023"),
                new EmployeeDT0("34", "EMP003", "Lê Văn C", "Nam", "20/03/1985", "0987654321", "111222333", "cđasadsadsad@gmail.com", "Đà Nẵng", "Phòng Kế Toán", "Kế toán trưởng", "Hoạt động", "10/01/2023", "11/01/2023"),
                new EmployeeDT0("35", "EMP004", "Phạm Thị D", "Nữ", "10/04/1995", "0971122334", "444555666", "ddsadsadsadsa@gmail.com", "Hải Phòng", "Phòng IT", "Nhân viên", "Nghỉ việc", "12/01/2023", "14/01/2023"),
                new EmployeeDT0("36", "EMP005", "Ngô Văn E", "Nam", "05/05/1993", "0933344455", "777888999", "edsadsadsads@gmail.com", "Cần Thơ", "Phòng Sales", "Trưởng phòng", "Hoạt động", "15/01/2023", "16/01/2023"),
                new EmployeeDT0("37", "EMP006", "Hoàng Thị F", "Nữ", "25/06/1991", "0956677889", "000111222", "fdsadsadsadsa@gmail.com", "Hà Nội", "Phòng Marketing", "Nhân viên", "Hoạt động", "17/01/2023", "18/01/2023"),
                new EmployeeDT0("38", "EMP007", "Vũ Văn G", "Nam", "30/07/1988", "0911223344", "333222111", "gdsadsdsdsadsa@gmail.com", "Hồ Chí Minh", "Phòng IT", "Quản lý", "Hoạt động", "19/01/2023", "20/01/2023"),
                new EmployeeDT0("39", "EMP008", "Đặng Thị H", "Nữ", "15/08/1994", "0944455566", "666555444", "hdsadsadsadsad@gmail.com", "Hải Dương", "Phòng HR", "Nhân viên", "Nghỉ việc", "21/01/2023", "22/01/2023"),
                new EmployeeDT0("40", "EMP009", "Phan Văn I", "Nam", "12/09/1987", "0922233344", "999888777", "idsadsadsadsa@gmail.com", "Quảng Ninh", "Phòng Kỹ Thuật", "Trưởng phòng", "Hoạt động", "23/01/2023", "24/01/2023"),
                new EmployeeDT0("41", "EMP010", "Lý Thị J", "Nữ", "01/10/1990", "0915566778", "123321456", "jdsadsdsadsa@gmail.com", "Vĩnh Phúc", "Phòng IT", "Nhân viên", "Hoạt động", "25/01/2023", "26/01/2023")

        );
        setupTable(masterData); // Khởi tạo bảng
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
        // Nếu masterData trống, tạo danh sách rỗng
        if (masterData == null || masterData.isEmpty()) {
            fixedFirstTable.getItems().clear();
            scrollableTable.getItems().clear();
            fixedLastTable.getItems().clear();
            return;
        }

        int fromIndex = (pageIndex - 1) * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, masterData.size());

        ObservableList<EmployeeDT0> pageData = FXCollections.observableArrayList(
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
    private void setupTable(ObservableList<EmployeeDT0> data) {
        // Khởi tạo các cột bảng
        if (fixedFirstTable.getColumns().isEmpty()) {
            fixedFirstTable.setMinWidth(210);
            TableColumn<EmployeeDT0, String> IDColumn = new TableColumn<>("ID");
            TableColumn<EmployeeDT0, String> employeeCodeColumn = new TableColumn<>("Mã Nhân viên");
            IDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getID()));
            employeeCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmployeecode()));
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
            birthdayColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBirthday()));
            mobileColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMobile()));
            cardIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCardId()));
            emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
            addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
            departmentNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartmentName()));
            roleNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoleName()));
            statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
            createDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreateDate()));
            updateDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUpdateDate()));

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

        }

        if (fixedLastTable.getColumns().isEmpty()) {
            TableColumn<EmployeeDT0, Void> actionColumn = new TableColumn<>("Hành động");
            actionColumn.setCellFactory(param -> new TableCell<>() {
                private final HBox actionBox = new HBox(10);

                {
                    actionBox.setAlignment(Pos.CENTER);
                    MFXFontIcon viewIcon = new MFXFontIcon("fas-eye", 18);
                    viewIcon.setStyle("-fx-cursor: hand;");
                    viewIcon.setColor(Color.FORESTGREEN);
                    viewIcon.setOnMouseClicked(event -> {
                        loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/fxml/EmployeeDetail.fxml"));
                        try {
                            loader.load();
                        } catch (IOException ex) {
                            Logger.getLogger(EditEmployeeController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        Parent parent = loader.getRoot();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(parent));
                        stage.initStyle(StageStyle.UTILITY);
                        stage.show();
                    });

                    MFXFontIcon editIcon = new MFXFontIcon("fas-pen-to-square", 18);
                    editIcon.setStyle("-fx-cursor: hand;");
                    editIcon.setColor(Color.BLUE);
                    editIcon.setOnMouseClicked(event -> {
                        loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/fxml/EditEmployee.fxml"));
                        try {
                            loader.load();
                        } catch (IOException ex) {
                            Logger.getLogger(EditEmployeeController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        Parent parent = loader.getRoot();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(parent));
                        stage.initStyle(StageStyle.UTILITY);
                        stage.show();
                    });

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
                    actionBox.getChildren().addAll(viewIcon, editIcon, deleteIcon);
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(actionBox);
                    }
                }

            });

            fixedLastTable.getColumns().add(actionColumn);
            fixedLastTable.setMinWidth(120);
            fixedLastTable.setMaxWidth(120);
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
}
