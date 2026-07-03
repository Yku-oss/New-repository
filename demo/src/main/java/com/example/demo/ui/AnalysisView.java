package com.example.demo.ui;

import com.example.demo.config.SpringContextHolder;
import com.example.demo.service.AnalysisService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Map;

public class AnalysisView extends VBox {

    private final AnalysisService analysisService = SpringContextHolder.getBean(AnalysisService.class);

    public AnalysisView() {
        setPadding(new Insets(20));
        setSpacing(20);
        setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("📊 数据分析");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 22));

        // Tab 布局
        TabPane tabPane = new TabPane();
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // 订阅统计
        Tab statsTab = new Tab("📈 订阅统计");
        statsTab.setContent(createStatsTable(
            analysisService.getSubscriptionStats(),
            "newspaperName", "订阅数", "totalRevenue", "总收入"
        ));
        statsTab.setClosable(false);

        // 客户行为分析
        Tab behaviorTab = new Tab("👤 客户行为分析");
        behaviorTab.setContent(createStatsTable(
            analysisService.getCustomerBehaviorAnalysis(),
            "customerName", "客户名", "orderCount", "订单数", "totalSpent", "总消费"
        ));
        behaviorTab.setClosable(false);

        // 库存周转
        Tab inventoryTab = new Tab("📦 库存周转");
        inventoryTab.setContent(createStatsTable(
            analysisService.getInventoryTurnoverStats(),
            "newspaperName", "报纸名", "stock", "库存", "totalIn", "总入库", "totalOut", "总出库"
        ));
        inventoryTab.setClosable(false);

        tabPane.getTabs().addAll(statsTab, behaviorTab, inventoryTab);

        getChildren().addAll(title, tabPane);
    }

    @SafeVarargs
    private <T> VBox createStatsTable(List<Map<String, Object>> data, String... columns) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));

        if (data.isEmpty()) {
            box.getChildren().add(new Label("暂无数据"));
            return box;
        }

        TableView<Map<String, Object>> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(data));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        for (int i = 0; i < columns.length; i += 2) {
            String key = columns[i];
            String displayName = columns[i + 1];
            TableColumn<Map<String, Object>, Object> col = new TableColumn<>(displayName);
            col.setCellValueFactory(cd -> {
                Object val = cd.getValue().get(key);
                return new SimpleObjectProperty<>(val);
            });
            table.getColumns().add(col);
        }

        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren().add(table);
        return box;
    }
}
