package com.bachnh.accesscontrolsystem.controller;
import com.bachnh.accesscontrolsystem.dto.EmployeeDT0;
import com.bachnh.accesscontrolsystem.model.Device;
import com.bachnh.accesscontrolsystem.model.Model;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
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
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class EmployeesController implements Initializable {
    @FXML
    private MFXPaginatedTableView<EmployeeDT0> paginated;
    @FXML
    private BorderPane borderPane;
    @FXML
    private MFXButton addEmployeeBtn;
    private FXMLLoader loader ;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPaginated();
        MFXScrollPane scrollPane = new MFXScrollPane();
        scrollPane.setFitToWidth(true);
        paginated.autosizeColumnsOnInitialization();

        When.onChanged(paginated.currentPageProperty())
                .then((oldValue, newValue) -> paginated.autosizeColumns())
                .listen();

    }
    private void setupPaginated() {
        // Khởi tạo các cột bảng
        MFXTableColumn<EmployeeDT0> IDColumn = new MFXTableColumn<>("ID", false, Comparator.comparing(EmployeeDT0::getID));
        MFXTableColumn<EmployeeDT0> employeeCodeColumn = new MFXTableColumn<>("Mã Nhân viên", true, Comparator.comparing(EmployeeDT0::getEmployeecode));
        MFXTableColumn<EmployeeDT0> fullnameColumn = new MFXTableColumn<>("Họ và Tên", true, Comparator.comparing(EmployeeDT0::getFullname));
        MFXTableColumn<EmployeeDT0> genderColumn = new MFXTableColumn<>("Giới tính", false, Comparator.comparing(EmployeeDT0::getGender));
        MFXTableColumn<EmployeeDT0> birthdayColumn = new MFXTableColumn<>("Ngày sinh", true, Comparator.comparing(EmployeeDT0::getBirthday));
        MFXTableColumn<EmployeeDT0> mobileColumn = new MFXTableColumn<>("Số điện thoại", true, Comparator.comparing(EmployeeDT0::getMobile));
        MFXTableColumn<EmployeeDT0> cardIdColumn = new MFXTableColumn<>("CMND/CCCD", true, Comparator.comparing(EmployeeDT0::getCardId));
        MFXTableColumn<EmployeeDT0> emailColumn = new MFXTableColumn<>("Email", true, Comparator.comparing(EmployeeDT0::getEmail));
        MFXTableColumn<EmployeeDT0> addressColumn = new MFXTableColumn<>("Địa chỉ", true, Comparator.comparing(EmployeeDT0::getAddress));
        MFXTableColumn<EmployeeDT0> departmentNameColumn = new MFXTableColumn<>("Phòng ban", true, Comparator.comparing(EmployeeDT0::getDepartmentName));
        MFXTableColumn<EmployeeDT0> roleNameColumn = new MFXTableColumn<>("Vị trí", true, Comparator.comparing(EmployeeDT0::getRoleName));
        MFXTableColumn<EmployeeDT0> statusColumn = new MFXTableColumn<>("Trạng thái", true, Comparator.comparing(EmployeeDT0::getStatus));
        MFXTableColumn<EmployeeDT0> createDateColumn = new MFXTableColumn<>("Ngày tạo", true, Comparator.comparing(EmployeeDT0::getCreateDate));
        MFXTableColumn<EmployeeDT0> updateDateColumn = new MFXTableColumn<>("Ngày cập nhật", true, Comparator.comparing(EmployeeDT0::getUpdateDate));

        IDColumn.setRowCellFactory(device -> new MFXTableRowCell<>(EmployeeDT0::getID));
        employeeCodeColumn.setRowCellFactory(employee -> new MFXTableRowCell<>(EmployeeDT0::getEmployeecode));
        fullnameColumn.setRowCellFactory(employee -> new MFXTableRowCell<>(EmployeeDT0::getFullname) {{
            setAlignment(Pos.CENTER_RIGHT);
        }});
        genderColumn.setRowCellFactory(device -> new MFXTableRowCell<>(EmployeeDT0::getGender));
        birthdayColumn.setRowCellFactory(device -> new MFXTableRowCell<>(EmployeeDT0::getBirthday));
        mobileColumn.setRowCellFactory(device -> new MFXTableRowCell<>(EmployeeDT0::getMobile));
        cardIdColumn.setRowCellFactory(device -> new MFXTableRowCell<>(EmployeeDT0::getCardId));
        emailColumn.setRowCellFactory(device -> new MFXTableRowCell<>(EmployeeDT0::getEmail));
        addressColumn.setRowCellFactory(device -> new MFXTableRowCell<>(EmployeeDT0::getAddress));
        departmentNameColumn.setRowCellFactory(device -> new MFXTableRowCell<>(EmployeeDT0::getDepartmentName));
        roleNameColumn.setRowCellFactory(device -> new MFXTableRowCell<>(EmployeeDT0::getRoleName));
        statusColumn.setRowCellFactory(device -> new MFXTableRowCell<>(EmployeeDT0::getStatus));
        createDateColumn.setRowCellFactory(device -> new MFXTableRowCell<>(EmployeeDT0::getCreateDate));
        updateDateColumn.setRowCellFactory(device -> new MFXTableRowCell<>(EmployeeDT0::getUpdateDate));
        ObservableList<EmployeeDT0> data = FXCollections.observableArrayList(
                new EmployeeDT0("1", "EMP001", "Nguyễn Văn A", "Nam", "01/01/1990", "0912345678", "123456789", "adsadsadsadsadsa@gmail.com", "Hà Nội", "Phòng IT", "Nhân viên", "Hoạt động", "01/01/2023", "02/01/2023"),
                new EmployeeDT0("2", "EMP002", "Trần Thị B", "Nữ", "15/02/1992", "0918765432", "987654321", "bdsadsadsadsadsa@gmail.com", "Hồ Chí Minh", "Phòng HR", "Quản lý", "Hoạt động", "05/01/2023", "06/01/2023"),
                new EmployeeDT0("3", "EMP003", "Lê Văn C", "Nam", "20/03/1985", "0987654321", "111222333", "cđasadsadsad@gmail.com", "Đà Nẵng", "Phòng Kế Toán", "Kế toán trưởng", "Hoạt động", "10/01/2023", "11/01/2023"),
                new EmployeeDT0("4", "EMP004", "Phạm Thị D", "Nữ", "10/04/1995", "0971122334", "444555666", "ddsadsadsadsa@gmail.com", "Hải Phòng", "Phòng IT", "Nhân viên", "Nghỉ việc", "12/01/2023", "14/01/2023"),
                new EmployeeDT0("5", "EMP005", "Ngô Văn E", "Nam", "05/05/1993", "0933344455", "777888999", "edsadsadsads@gmail.com", "Cần Thơ", "Phòng Sales", "Trưởng phòng", "Hoạt động", "15/01/2023", "16/01/2023"),
                new EmployeeDT0("6", "EMP006", "Hoàng Thị F", "Nữ", "25/06/1991", "0956677889", "000111222", "fdsadsadsadsa@gmail.com", "Hà Nội", "Phòng Marketing", "Nhân viên", "Hoạt động", "17/01/2023", "18/01/2023"),
                new EmployeeDT0("7", "EMP007", "Vũ Văn G", "Nam", "30/07/1988", "0911223344", "333222111", "gdsadsdsdsadsa@gmail.com", "Hồ Chí Minh", "Phòng IT", "Quản lý", "Hoạt động", "19/01/2023", "20/01/2023"),
                new EmployeeDT0("8", "EMP008", "Đặng Thị H", "Nữ", "15/08/1994", "0944455566", "666555444", "hdsadsadsadsad@gmail.com", "Hải Dương", "Phòng HR", "Nhân viên", "Nghỉ việc", "21/01/2023", "22/01/2023"),
                new EmployeeDT0("9", "EMP009", "Phan Văn I", "Nam", "12/09/1987", "0922233344", "999888777", "idsadsadsadsa@gmail.com", "Quảng Ninh", "Phòng Kỹ Thuật", "Trưởng phòng", "Hoạt động", "23/01/2023", "24/01/2023"),
                new EmployeeDT0("10", "EMP010", "Lý Thị J", "Nữ", "01/10/1990", "0915566778", "123321456", "jdsadsdsadsa@gmail.com", "Vĩnh Phúc", "Phòng IT", "Nhân viên", "Hoạt động", "25/01/2023", "26/01/2023")
        );
        // Thêm cột hành động
        MFXTableColumn<EmployeeDT0> actionColumn = new MFXTableColumn<>("Hành động", true);
        actionColumn.setRowCellFactory(employee -> {
            return new MFXTableRowCell<>(employee1 -> "") {
                @Override
                public void update(EmployeeDT0 item) {
                    super.update(item);
                    if (item == null) {
                        setGraphic(null);
                    }
                    else
                    {
                        HBox actionBox = new HBox(10);
                        actionBox.setAlignment(Pos.CENTER);

                        // Tạo icon Xem
                        MFXFontIcon viewIcon = new MFXFontIcon("fas-eye", 24);
                        viewIcon.setStyle("-fx-cursor: hand;");
                        viewIcon.setColor(Color.FORESTGREEN);
                        viewIcon.setOnMouseClicked(event -> {
                            loader = new FXMLLoader ();
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

                        // Tạo icon Sửa
                        MFXFontIcon editIcon = new MFXFontIcon("fas-pen-to-square", 24);
                        editIcon.setStyle("-fx-cursor: hand;");
                        editIcon.setColor(Color.BLUE);
                        editIcon.setOnMouseClicked(event -> {
                            loader = new FXMLLoader ();
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

                        // Tạo icon Xóa
                        MFXFontIcon deleteIcon = new MFXFontIcon("fas-trash-can", 24);
                        deleteIcon.setStyle("-fx-cursor: hand;");
                        deleteIcon.setColor(Color.RED);
                        deleteIcon.setOnMouseClicked(event -> {
                            Stage currentStage = (Stage) borderPane.getScene().getWindow();
                            MFXFontIcon warnIcon = new MFXFontIcon("fas-circle-exclamation", 18);
                            warnIcon.setColor(Color.RED);
                            MFXGenericDialog dialogContent = MFXGenericDialogBuilder.build()
                                    .setContentText("Bạn có chắc chăn muốn xóa người dùng này")
//                                    .makeScrollable(true)
                                    .setHeaderIcon(warnIcon)
                                    .setHeaderText("Xác nhận xóa")
                                    .get();
                            MFXStageDialog dialog = MFXGenericDialogBuilder.build(dialogContent)
                                    .toStageDialogBuilder()
                                    .initOwner(currentStage)
                                    .initModality(Modality.APPLICATION_MODAL)
                                    .setDraggable(true)
//                                    .setTitle("Xác nhận xóa")
                                    .setOwnerNode(borderPane)
                                    .setScrimPriority(ScrimPriority.WINDOW)
                                    .setScrimOwner(true)
                                    .get();
                            dialogContent.addActions(
                                    Map.entry(new MFXButton("Xác nhận"), e -> {
                                        System.out.println("Xóa thiết bị: " + employee.getFullname());
                                        dialog.close();
                                    }),
                                    Map.entry(new MFXButton("Hủy"), e -> dialog.close())
                            );
                            dialog.showDialog();
                        });
                        actionBox.getChildren().addAll(viewIcon, editIcon, deleteIcon);
                        setGraphic(actionBox);
                    }
                }
            };
        });

        // Thêm tất cả cột vào bảng
        paginated.getTableColumns().addAll(IDColumn, employeeCodeColumn, fullnameColumn,
                genderColumn, birthdayColumn,mobileColumn,cardIdColumn,emailColumn,
                addressColumn,departmentNameColumn,roleNameColumn,
                statusColumn,createDateColumn,updateDateColumn, actionColumn);
        paginated.getFilters().clear();
//        paginated.getFilters().addAll(
//                new IntegerFilter<>("ID", Device::getID),
//                new StringFilter<>("Name", Device::getName),
//                new StringFilter<>("IP", Device::getIP),
//                new StringFilter<>("Owner", Device::getOwner),
//                new EnumFilter<>("State", Device::getState, Device.State.class)
//        );

        // Set danh sách thiết bị
        paginated.setItems(data);
        addEmployee();

    }
    private void addEmployee() {
        addEmployeeBtn.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("/fxml/AddEmployee.fxml"));
            try {
                loader.load();
            } catch (IOException ex) {
                Logger.getLogger(AddEmployeeController.class.getName()).log(Level.SEVERE, null, ex);
            }
            Parent parent = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        });
    }

}

