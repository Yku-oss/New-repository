package com.example.demo.ui;

import com.example.demo.config.SpringContextHolder;
import com.example.demo.entity.InventoryLog;
import com.example.demo.entity.Newspaper;
import com.example.demo.service.InventoryLogService;
import com.example.demo.service.NewspaperService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDateTime;

public class InventoryLogTableView extends VBox {

    private final InventoryLogService inventoryLogService = SpringContextHolder.getBean(InventoryLogService.class);
    private final NewspaperService newspaperService = SpringContextHolder.getBean(NewspaperService.class);
    private final TableView<InventoryLog> table = new TableView<>();
    private final ObservableList<InventoryLog> data = FXCollections.observableArrayList();

    public InventoryLogTableView() {
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("📦 库存日志");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 22));

        HBox toolbar = new HBox(10);
        Button refreshBtn = new Button("🔄 刷新");
        refreshBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 13; -fx-padding: 8 16;");
        toolbar.getChildren().addAll(refreshBtn);

        TableColumn<InventoryLog, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id")); idCol.setPrefWidth(50);

        TableColumn<InventoryLog, String> newspaperCol = new TableColumn<>("报纸");
        newspaperCol.setCellValueFactory(cd -> {
            Newspaper n = newspaperService.getById(cd.getValue().getNewspaperId());
            return new SimpleStringProperty(n != null ? n.getName() : "");
        });
        newspaperCol.setPrefWidth(180);

        TableColumn<InventoryLog, Integer> qtyCol = new TableColumn<>("变动数量");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("changeQuantity")); qtyCol.setPrefWidth(100);

        TableColumn<InventoryLog, String> typeCol = new TableColumn<>("类型");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type")); typeCol.setPrefWidth(80);

        TableColumn<InventoryLog, String> remarkCol = new TableColumn<>("备注");
        remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark")); remarkCol.setPrefWidth(250);

        TableColumn<InventoryLog, LocalDateTime> timeCol = new TableColumn<>("时间");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("createTime")); timeCol.setPrefWidth(180);

        table.getColumns().addAll(idCol, newspaperCol, qtyCol, typeCol, remarkCol, timeCol);
        table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);

        refreshBtn.setOnAction(e -> refresh());

        getChildren().addAll(title, toolbar, table);
        refresh();
    }

    private void refresh() { data.setAll(inventoryLogService.getAll()); }
}
