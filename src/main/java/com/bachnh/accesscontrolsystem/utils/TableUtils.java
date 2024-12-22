package com.bachnh.accesscontrolsystem.utils;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TableUtils {
    public static void disableSorting(TableView<?> tableView) {
        for (TableColumn<?, ?> column : tableView.getColumns()) {
            column.setSortable(false); // Disable sorting for each column
        }
    }

    public static void syncScrollBars(TableView<?>... tables) {
        // Lấy danh sách các thanh cuộn dọc từ các bảng được cung cấp
        List<ScrollBar> scrollBars = Arrays.stream(tables)
                .map(TableUtils::getVerticalScrollBar)
                .filter(Objects::nonNull)
                .toList();

        // Đồng bộ hóa giá trị các thanh cuộn dọc (nếu có từ 2 bảng trở lên)
        if (!scrollBars.isEmpty()) {
            for (int i = 1; i < scrollBars.size(); i++) {
                scrollBars.get(i).valueProperty().bindBidirectional(scrollBars.get(0).valueProperty());
            }
        }
    }

    // Phương thức tìm thanh cuộn dọc của một TableView
    private static ScrollBar getVerticalScrollBar(TableView<?> tableView) {
        return tableView.lookupAll(".scroll-bar").stream()
                .filter(node -> node instanceof ScrollBar)
                .map(node -> (ScrollBar) node)
                .filter(scrollBar -> scrollBar.getOrientation() == Orientation.VERTICAL)
                .findFirst()
                .orElse(null);
    }

    public static <T> void synchronizeTableSelection(TableView<T>... tables) {
        // Kiểm tra danh sách các bảng, cần ít nhất 2 bảng để đồng bộ
        if (tables == null || tables.length < 2) return;

        for (TableView<T> table : tables) {
            table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Đồng bộ lựa chọn với các bảng khác
                    for (TableView<T> otherTable : tables) {
                        if (otherTable != table) {
                            otherTable.getSelectionModel().select(newValue);
                        }
                    }
                }
            });
        }
    }
}
