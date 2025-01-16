package com.bachnh.accesscontrolsystem.controller;
import com.bachnh.accesscontrolsystem.dto.GuestDTO;
import com.bachnh.accesscontrolsystem.entity.Guest;
import com.bachnh.accesscontrolsystem.repository.GuestRepository;
import com.bachnh.accesscontrolsystem.utils.TableUtils;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPagination;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
@Component
public class GuestsController implements Initializable {
    @FXML
    private TableView<GuestDTO> fixedFirstTable;
    @FXML
    private TableView<GuestDTO> scrollableTable;
    @FXML
    private TableView<GuestDTO> fixedLastTable;
    @FXML
    private HBox tableContainer;
    @FXML
    private BorderPane borderPane;
    @FXML
    private MFXButton addGuestBtn;
    @FXML
    private MFXPagination paginator;
    private FXMLLoader loader;
    private ObservableList<GuestDTO> masterData; // Danh sách dữ liệu gốc
    private final int ROWS_PER_PAGE = 30;
    private final Map<Integer, ObservableList<GuestDTO>> pageCache = new HashMap<>();
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeData();
        setupPaginated();
        Platform.runLater(() -> {
            TableUtils.syncScrollBars(fixedFirstTable, scrollableTable, fixedLastTable);
            TableUtils.synchronizeTableSelection(fixedFirstTable, scrollableTable, fixedLastTable);
        });
        addGuestBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> addGuest());;

    }

    private void initializeData() {
        if (guestRepository == null){
            return;
        }
        List<Guest> guests = guestRepository.findAll();
        AtomicInteger count = new AtomicInteger(1);
        List<GuestDTO> data = guests.stream()
                .map(guest -> new GuestDTO(
                        count.getAndIncrement() ,
                        guest.getGuestCode() ,
                        guest.getGuestName(),
                        guest.getGender() ,
                        guest.getCardId(),
                        guest.getBirthday(),
                        guest.getMobile(),
                        guest.getEmail(),
                        guest.getAddress(),
                        guest.getStatus(),
                        guest.getCreateDate(),
                        guest.getUpdateDate()

                ))
                .toList();
        masterData = FXCollections.observableArrayList(data);
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

        ObservableList<GuestDTO> pageData = FXCollections.observableArrayList(
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
    private void setupTable(ObservableList<GuestDTO> data) {
        // Khởi tạo các cột bảng
        if (fixedFirstTable.getColumns().isEmpty()) {
            fixedFirstTable.setMinWidth(210);
            TableColumn<GuestDTO, String> IDColumn = new TableColumn<>("ID");
            TableColumn<GuestDTO, String> guestCodeColumn = new TableColumn<>("Mã Khách");
            IDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID())));
            guestCodeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGuestCode()));
            guestCodeColumn.setMinWidth(150);
            fixedFirstTable.getColumns().addAll(IDColumn, guestCodeColumn);
            fixedFirstTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            TableUtils.disableSorting(fixedFirstTable);
        }

        if (scrollableTable.getColumns().isEmpty()) {
            TableColumn<GuestDTO, String> guestNameColumn = new TableColumn<>("Họ và Tên");
            TableColumn<GuestDTO, String> genderColumn = new TableColumn<>("Giới tính");
            TableColumn<GuestDTO, String> cardIdColumn = new TableColumn<>("CMND/CCCD");
            TableColumn<GuestDTO, String> birthdayColumn = new TableColumn<>("Ngày sinh");
            TableColumn<GuestDTO, String> mobileColumn = new TableColumn<>("Số điện thoại");
            TableColumn<GuestDTO, String> emailColumn = new TableColumn<>("Email");
            TableColumn<GuestDTO, String> addressColumn = new TableColumn<>("Địa chỉ");
            TableColumn<GuestDTO, String> statusColumn = new TableColumn<>("Trạng thái");
            TableColumn<GuestDTO, String> createDateColumn = new TableColumn<>("Ngày tạo");
            TableColumn<GuestDTO, String> updateDateColumn = new TableColumn<>("Ngày cập nhật");

            guestNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGuestName()));
            genderColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender()));
            DateTimeFormatter formatterBirthday = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//            birthdayColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBirthday()));

            birthdayColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(
                            cellData.getValue().getBirthday() != null
                                    ? cellData.getValue().getBirthday().format(formatterBirthday)
                                    : ""
                    )
            );
            mobileColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMobile()));
            cardIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCardId()));
            emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
            addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
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

            guestNameColumn.setMinWidth(200);
            genderColumn.setMinWidth(100);
            birthdayColumn.setMinWidth(150);
            mobileColumn.setMinWidth(150);
            cardIdColumn.setMinWidth(150);
            emailColumn.setMinWidth(250);
            addressColumn.setMinWidth(200);
            statusColumn.setMinWidth(150);
            createDateColumn.setMinWidth(150);
            updateDateColumn.setMinWidth(150);

            scrollableTable.getColumns().addAll(guestNameColumn, mobileColumn, cardIdColumn,
                    emailColumn,genderColumn, birthdayColumn, addressColumn, statusColumn, createDateColumn,
                    updateDateColumn);
            scrollableTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            TableUtils.disableSorting(scrollableTable);

        }

        if (fixedLastTable.getColumns().isEmpty()) {
            TableColumn<GuestDTO, Void> actionColumn = new TableColumn<>("Hành động");
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
                        loader.setLocation(getClass().getResource("/fxml/EditGuest.fxml"));
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
//                                System.out.println("Xóa nhân viên: " + getTableView().getItems().get(getIndex()).getGuestCode());
                                Guest deleteByRoleCode = guestRepository.findByGuestCode(getTableView().getItems().get(getIndex()).getGuestCode());
                                guestRepository.delete(deleteByRoleCode);
                                Platform.runLater(()-> {
                                    initializeData();
                                    setupPaginated();
                                });
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
    private void addGuest() {
        FXMLLoader loader = new FXMLLoader ();
        loader.setLocation(getClass().getResource("/fxml/AddGuest.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
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
        stage.setOnHidden(event -> {
            // Cập nhật lại dữ liệu bảng
            initializeData();
            setupPaginated();
        });
    }
}
