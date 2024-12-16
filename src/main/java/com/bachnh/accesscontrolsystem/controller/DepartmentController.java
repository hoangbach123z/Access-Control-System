package com.bachnh.accesscontrolsystem.controller;

import com.bachnh.accesscontrolsystem.model.Device;
import com.bachnh.accesscontrolsystem.model.Model;
import com.bachnh.accesscontrolsystem.model.Person;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.materialfx.filter.IntegerFilter;
import io.github.palexdev.materialfx.filter.StringFilter;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

public class DepartmentController implements Initializable {
    @FXML
    private MFXPaginatedTableView<Device> paginated;
    @FXML
    private MFXTableView<Person> table;
    @FXML
    private BorderPane borderPane;
    @FXML
    private MFXButton addDepartmentBtn;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        setupPaginated();

//        paginated.autosizeColumnsOnInitialization();
//        When.onChanged(paginated.currentPageProperty())
//                .then((oldValue, newValue) -> paginated.autosizeColumns())
//                .listen();
        setupTable();
        table.autosizeColumnsOnInitialization();
    }
    private void setupTable() {
        MFXTableColumn<Person> nameColumn = new MFXTableColumn<>("Name", true, Comparator.comparing(Person::getName));
        MFXTableColumn<Person> surnameColumn = new MFXTableColumn<>("Surname", true, Comparator.comparing(Person::getSurname));
        MFXTableColumn<Person> ageColumn = new MFXTableColumn<>("Age", true, Comparator.comparing(Person::getAge));

        nameColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Person::getName));
        surnameColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Person::getSurname));
        ageColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Person::getAge) {{
            setAlignment(Pos.CENTER_RIGHT);
        }});
        ageColumn.setAlignment(Pos.CENTER_RIGHT);
        // Thêm cột hành động
        MFXTableColumn<Person> actionColumn = new MFXTableColumn<>("", false);
        actionColumn.setRowCellFactory(person -> {
            return new MFXTableRowCell<>(person1 -> "") {
                private HBox actionBox;
                @Override
                public void update(Person item) {
                    super.update(item);
                    if (item == null) {
                        setGraphic(null);
                    }
                    else
                    {
                        actionBox = new HBox(10);
                        actionBox.setAlignment(Pos.CENTER);

                        // Tạo icon Sửa
                        MFXFontIcon editIcon = new MFXFontIcon("fas-pen-to-square", 24);
                        editIcon.setStyle("-fx-cursor: hand;");
                        editIcon.setColor(Color.BLUE);
                        editIcon.setOnMouseClicked(event -> {
                            FXMLLoader loader = new FXMLLoader ();
                            loader.setLocation(getClass().getResource("/fxml/EditDepartment.fxml"));
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
                                        System.out.println("Xóa thiết bị: " + person.getName());
                                        dialog.close();
                                    }),
                                    Map.entry(new MFXButton("Hủy"), e -> dialog.close())
                            );
                            dialog.showDialog();
                        });
                        actionBox.getChildren().addAll(editIcon, deleteIcon);
                        setGraphic(actionBox);
                    }
                }
            };
        });
        table.getTableColumns().addAll(nameColumn, surnameColumn, ageColumn,actionColumn);
        table.getFilters().addAll(
                new StringFilter<>("Name", Person::getName),
                new StringFilter<>("Surname", Person::getSurname),
                new IntegerFilter<>("Age", Person::getAge)
        );
        table.setItems(Model.people);

        addDepartment();
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
