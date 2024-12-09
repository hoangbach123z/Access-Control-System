package com.bachnh.accesscontrolsystem.controller;
import com.bachnh.accesscontrolsystem.model.Device;
import com.bachnh.accesscontrolsystem.model.Model;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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
    private MFXPaginatedTableView<Device> paginated;
    @FXML
    private GridPane gridPane;
    @FXML
    private MFXButton addEmployeeBtn;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPaginated();

        paginated.autosizeColumnsOnInitialization();
        When.onChanged(paginated.currentPageProperty())
                .then((oldValue, newValue) -> paginated.autosizeColumns())
                .listen();

    }
    private void setupPaginated() {
        // Khởi tạo các cột bảng
        MFXTableColumn<Device> idColumn = new MFXTableColumn<>("ID", false, Comparator.comparing(Device::getID));
        MFXTableColumn<Device> nameColumn = new MFXTableColumn<>("Name", false, Comparator.comparing(Device::getName));
        MFXTableColumn<Device> ipColumn = new MFXTableColumn<>("IP", false, Comparator.comparing(Device::getIP));
        MFXTableColumn<Device> ownerColumn = new MFXTableColumn<>("Owner", false, Comparator.comparing(Device::getOwner));
        MFXTableColumn<Device> stateColumn = new MFXTableColumn<>("State", false, Comparator.comparing(Device::getState));

        idColumn.setRowCellFactory(device -> new MFXTableRowCell<>(Device::getID));
        nameColumn.setRowCellFactory(device -> new MFXTableRowCell<>(Device::getName));
        ipColumn.setRowCellFactory(device -> new MFXTableRowCell<>(Device::getIP) {{
            setAlignment(Pos.CENTER_RIGHT);
        }});
        ownerColumn.setRowCellFactory(device -> new MFXTableRowCell<>(Device::getOwner));
        stateColumn.setRowCellFactory(device -> new MFXTableRowCell<>(Device::getState));

        // Thêm cột hành động
        MFXTableColumn<Device> actionColumn = new MFXTableColumn<>("", false);
        actionColumn.setRowCellFactory(device -> {
            return new MFXTableRowCell<>(device1 -> "") {
                private HBox actionBox;
                @Override
                public void update(Device item) {
                    super.update(item);
                    if (item == null) {
                        setGraphic(null);
                    }
                    else
                    {
                        actionBox = new HBox(10);
                        actionBox.setAlignment(Pos.CENTER);

                        // Tạo icon Xem
                        MFXFontIcon viewIcon = new MFXFontIcon("fas-eye", 24);
                        viewIcon.setStyle("-fx-cursor: hand;");
                        viewIcon.setColor(Color.FORESTGREEN);
                        viewIcon.setOnMouseClicked(event -> {
                            FXMLLoader loader = new FXMLLoader ();
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
                            FXMLLoader loader = new FXMLLoader ();
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
                            Stage currentStage = (Stage) gridPane.getScene().getWindow();
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
                                    .setOwnerNode(gridPane)
                                    .setScrimPriority(ScrimPriority.WINDOW)
                                    .setScrimOwner(true)
                                    .get();
                            dialogContent.addActions(
                                    Map.entry(new MFXButton("Xác nhận"), e -> {
                                        System.out.println("Xóa thiết bị: " + device.getName());
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
        paginated.getTableColumns().addAll(idColumn, nameColumn, ipColumn, ownerColumn, stateColumn, actionColumn);
        paginated.getFilters().addAll(
                new IntegerFilter<>("ID", Device::getID),
                new StringFilter<>("Name", Device::getName),
                new StringFilter<>("IP", Device::getIP),
                new StringFilter<>("Owner", Device::getOwner),
                new EnumFilter<>("State", Device::getState, Device.State.class)
        );

        // Set danh sách thiết bị
        paginated.setItems(Model.devices);
        addEmployee();

    }
    private void addEmployee() {
        addEmployeeBtn.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            FXMLLoader loader = new FXMLLoader ();
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

