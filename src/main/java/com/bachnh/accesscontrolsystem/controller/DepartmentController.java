package com.bachnh.accesscontrolsystem.controller;

import com.bachnh.accesscontrolsystem.dto.AccessControlDTO;
import com.bachnh.accesscontrolsystem.dto.DepartmentDTO;
import com.bachnh.accesscontrolsystem.dto.RoleDTO;
import com.bachnh.accesscontrolsystem.model.Device;
import com.bachnh.accesscontrolsystem.model.Model;
import com.bachnh.accesscontrolsystem.model.Person;
import com.bachnh.accesscontrolsystem.utils.TableUtils;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
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

public class DepartmentController implements Initializable {
    @FXML
    private TableView<DepartmentDTO> fixedFirstTable;
    @FXML
    private TableView<DepartmentDTO> scrollableTable;
    @FXML
    private TableView<DepartmentDTO> fixedLastTable;
    @FXML
    private HBox tableContainer;
    @FXML
    private BorderPane borderPane;
    @FXML
    private MFXButton addDepartmentBtn;
    @FXML
    private MFXPagination paginator;
    private FXMLLoader loader;
    private ObservableList<DepartmentDTO> masterData; // Danh sách dữ liệu gốc
    private final int ROWS_PER_PAGE = 30;
    private final Map<Integer, ObservableList<DepartmentDTO>> pageCache = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeData();
        setupPaginated();
        Platform.runLater(() -> {
            TableUtils.syncScrollBars(fixedFirstTable, scrollableTable, fixedLastTable);
            TableUtils.synchronizeTableSelection(fixedFirstTable, scrollableTable, fixedLastTable);
        });
        addDepartment();
    }

    private void initializeData() {
        masterData = FXCollections.observableArrayList(


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

        ObservableList<DepartmentDTO> pageData = FXCollections.observableArrayList(
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
    private void setupTable(ObservableList<DepartmentDTO> data) {
        // Khởi tạo các cột bảng
        if (fixedFirstTable.getColumns().isEmpty()) {
            fixedFirstTable.setMinWidth(210);
            TableColumn<DepartmentDTO, String> IDColumn = new TableColumn<>("ID");
            TableColumn<DepartmentDTO, String> departmentCodeColumn = new TableColumn<>("Mã Phòng ban");
            IDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getID()));
            departmentCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartmentCode()));
            departmentCodeColumn.setMinWidth(150);
            fixedFirstTable.getColumns().addAll(IDColumn, departmentCodeColumn);
            fixedFirstTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            TableUtils.disableSorting(fixedFirstTable);
        }

        if (scrollableTable.getColumns().isEmpty()) {
            TableColumn<DepartmentDTO, String> departmentNameColumn = new TableColumn<>("Phòng ban");
            TableColumn<DepartmentDTO, String> statusColumn = new TableColumn<>("Trạng thái");
            TableColumn<DepartmentDTO, String> createDateColumn = new TableColumn<>("Ngày tạo");
            TableColumn<DepartmentDTO, String> updateDateColumn = new TableColumn<>("Ngày cập nhật");


            departmentNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartmentName()));
            statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
            createDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreateDate()));
            updateDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUpdateDate()));


            departmentNameColumn.setMinWidth(150);
            statusColumn.setMinWidth(100);
            createDateColumn.setMinWidth(150);
            updateDateColumn.setMinWidth(150);

            scrollableTable.getColumns().addAll(departmentNameColumn, statusColumn, createDateColumn, updateDateColumn);
            scrollableTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            TableUtils.disableSorting(scrollableTable);

        }

        if (fixedLastTable.getColumns().isEmpty()) {
            TableColumn<DepartmentDTO, Void> actionColumn = new TableColumn<>("Hành động");
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
                                System.out.println("Xóa nhân viên: " + getTableView().getItems().get(getIndex()).getDepartmentCode());
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
    private void addDepartment() {
        addDepartmentBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            FXMLLoader loader = new FXMLLoader ();
            loader.setLocation(getClass().getResource("/fxml/AddDepartment.fxml"));
            try {
                loader.load();
            } catch (IOException ex) {
                Logger.getLogger(AddDepartmentController.class.getName()).log(Level.SEVERE, null, ex);
            }
            Parent parent = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        });
    }

}
