package com.example.demo.ui;

import com.example.demo.config.SpringContextHolder;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class NewspaperTableView extends VBox {

    private final NewspaperService newspaperService = SpringContextHolder.getBean(NewspaperService.class);
    private final CategoryService categoryService = SpringContextHolder.getBean(CategoryService.class);
    private final SupplierService supplierService = SpringContextHolder.getBean(SupplierService.class);
    private final TableView<Newspaper> table = new TableView<>();
    private final ObservableList<Newspaper> data = FXCollections.observableArrayList();

    public NewspaperTableView() {
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("📰 报纸管理");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 22));

        // 工具栏
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button addBtn = new Button("➕ 新增报纸");
        Button refreshBtn = new Button("🔄 刷新");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 13; -fx-padding: 8 16;");
        refreshBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 13; -fx-padding: 8 16;");

        toolbar.getChildren().addAll(addBtn, refreshBtn);

        // 表格列
        TableColumn<Newspaper, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Newspaper, String> nameCol = new TableColumn<>("报纸名称");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Newspaper, BigDecimal> priceCol = new TableColumn<>("价格");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(80);

        TableColumn<Newspaper, String> periodCol = new TableColumn<>("刊期");
        periodCol.setCellValueFactory(new PropertyValueFactory<>("period"));
        periodCol.setPrefWidth(80);

        TableColumn<Newspaper, Integer> stockCol = new TableColumn<>("库存");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        stockCol.setPrefWidth(80);

        TableColumn<Newspaper, String> categoryCol = new TableColumn<>("分类");
        categoryCol.setCellValueFactory(cellData -> {
            Integer catId = cellData.getValue().getCategoryId();
            if (catId != null) {
                Category cat = categoryService.getById(catId);
                return new SimpleStringProperty(cat != null ? cat.getName() : "");
            }
            return new SimpleStringProperty("");
        });
        categoryCol.setPrefWidth(100);

        TableColumn<Newspaper, String> supplierCol = new TableColumn<>("供应商");
        supplierCol.setCellValueFactory(cellData -> {
            Integer supId = cellData.getValue().getSupplierId();
            if (supId != null) {
                Supplier sup = supplierService.getById(supId);
                return new SimpleStringProperty(sup != null ? sup.getName() : "");
            }
            return new SimpleStringProperty("");
        });
        supplierCol.setPrefWidth(120);

        TableColumn<Newspaper, Boolean> recommendedCol = new TableColumn<>("推荐");
        recommendedCol.setCellValueFactory(new PropertyValueFactory<>("recommended"));
        recommendedCol.setPrefWidth(60);

        TableColumn<Newspaper, String> descCol = new TableColumn<>("描述");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(200);

        TableColumn<Newspaper, Void> actionCol = new TableColumn<>("操作");
        actionCol.setPrefWidth(160);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("编辑");
            private final Button delBtn = new Button("删除");
            private final HBox pane = new HBox(4, editBtn, delBtn);

            {
                pane.setAlignment(Pos.CENTER);
                editBtn.setStyle("-fx-background-color: #d48806; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 3 10; -fx-background-radius: 3;");
                delBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 3 10; -fx-background-radius: 3;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Newspaper n = getTableView().getItems().get(getIndex());
                    editBtn.setOnAction(e -> showEditDialog(n));
                    delBtn.setOnAction(e -> {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "确认删除「" + n.getName() + "」？");
                        confirm.showAndWait().ifPresent(r -> {
                            if (r == ButtonType.OK) {
                                newspaperService.delete(n.getId());
                                refresh();
                            }
                        });
                    });
                    setGraphic(pane);
                }
            }
        });

        table.getColumns().addAll(idCol, nameCol, priceCol, periodCol, stockCol, categoryCol, supplierCol, recommendedCol, descCol, actionCol);
        table.setItems(data);
        table.setRowFactory(tv -> {
            TableRow<Newspaper> row = new TableRow<>();
            row.setPrefHeight(36);
            return row;
        });
        VBox.setVgrow(table, Priority.ALWAYS);

        refreshBtn.setOnAction(e -> refresh());
        addBtn.setOnAction(e -> showEditDialog(null));

        getChildren().addAll(title, toolbar, table);

        refresh();
    }

    private void refresh() {
        data.setAll(newspaperService.getAll());
    }

    private void showEditDialog(Newspaper newspaper) {
        boolean isNew = newspaper == null;
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(isNew ? "新增报纸" : "编辑报纸");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));

        TextField nameField = new TextField(isNew ? "" : newspaper.getName());
        TextField priceField = new TextField(isNew ? "" : newspaper.getPrice().toString());
        ComboBox<String> periodCombo = new ComboBox<>(
                FXCollections.observableArrayList("日报", "周报", "月刊", "季刊"));
        periodCombo.setValue(isNew ? "日报" : newspaper.getPeriod());
        TextField stockField = new TextField(isNew ? "0" : String.valueOf(newspaper.getStock()));
        ComboBox<Category> categoryCombo = new ComboBox<>(
                FXCollections.observableArrayList(categoryService.getAll()));
        if (!isNew && newspaper.getCategoryId() != null) {
            categoryCombo.setValue(categoryService.getById(newspaper.getCategoryId()));
        }
        ComboBox<Supplier> supplierCombo = new ComboBox<>(
                FXCollections.observableArrayList(supplierService.getAll()));
        if (!isNew && newspaper.getSupplierId() != null) {
            supplierCombo.setValue(supplierService.getById(newspaper.getSupplierId()));
        }
        CheckBox recommendedCheck = new CheckBox("推荐");
        recommendedCheck.setSelected(isNew ? false : newspaper.getRecommended());
        TextArea descArea = new TextArea(isNew ? "" : newspaper.getDescription());
        descArea.setPrefRowCount(3);
        descArea.setPrefWidth(300);

        form.add(new Label("报纸名称:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("价格:"), 0, 1);
        form.add(priceField, 1, 1);
        form.add(new Label("刊期:"), 0, 2);
        form.add(periodCombo, 1, 2);
        form.add(new Label("库存:"), 0, 3);
        form.add(stockField, 1, 3);
        form.add(new Label("分类:"), 0, 4);
        form.add(categoryCombo, 1, 4);
        form.add(new Label("供应商:"), 0, 5);
        form.add(supplierCombo, 1, 5);
        form.add(recommendedCheck, 1, 6);
        form.add(new Label("描述:"), 0, 7);
        form.add(descArea, 1, 7);

        Button saveBtn = new Button("💾 保存");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5; -fx-font-size: 13;");
        saveBtn.setOnAction(e -> {
            try {
                Newspaper n = isNew ? new Newspaper() : newspaper;
                n.setName(nameField.getText());
                n.setPrice(new BigDecimal(priceField.getText()));
                n.setPeriod(periodCombo.getValue());
                n.setStock(Integer.parseInt(stockField.getText()));
                Category selectedCat = categoryCombo.getValue();
                n.setCategoryId(selectedCat != null ? selectedCat.getId() : null);
                Supplier selectedSup = supplierCombo.getValue();
                n.setSupplierId(selectedSup != null ? selectedSup.getId() : null);
                n.setRecommended(recommendedCheck.isSelected());
                n.setDescription(descArea.getText());

                if (isNew) {
                    newspaperService.add(n);
                } else {
                    newspaperService.update(n);
                }
                refresh();
                dialog.close();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "输入数据有误: " + ex.getMessage()).show();
            }
        });

        Button cancelBtn = new Button("✖ 取消");
        cancelBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 6; -fx-font-size: 14;");
        cancelBtn.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(10, saveBtn, cancelBtn);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        VBox root = new VBox(10, form, buttons);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
