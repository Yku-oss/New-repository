package com.example.demo.ui;

import com.example.demo.config.SpringContextHolder;
import com.example.demo.entity.Supplier;
import com.example.demo.service.SupplierService;
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

public class SupplierTableView extends VBox {

    private final SupplierService supplierService = SpringContextHolder.getBean(SupplierService.class);
    private final TableView<Supplier> table = new TableView<>();
    private final ObservableList<Supplier> data = FXCollections.observableArrayList();

    public SupplierTableView() {
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("🏢 供应商管理");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 22));

        HBox toolbar = new HBox(10);
        Button addBtn = new Button("➕ 新增供应商");
        Button refreshBtn = new Button("🔄 刷新");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 13; -fx-padding: 8 16;");
        refreshBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 13; -fx-padding: 8 16;");
        toolbar.getChildren().addAll(addBtn, refreshBtn);

        TableColumn<Supplier, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Supplier, String> nameCol = new TableColumn<>("供应商名称");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(180);

        TableColumn<Supplier, String> contactCol = new TableColumn<>("联系人");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        contactCol.setPrefWidth(100);

        TableColumn<Supplier, String> phoneCol = new TableColumn<>("电话");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(140);

        TableColumn<Supplier, String> addressCol = new TableColumn<>("地址");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressCol.setPrefWidth(200);

        TableColumn<Supplier, Void> actionCol = new TableColumn<>("操作");
        actionCol.setPrefWidth(160);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("编辑");
            private final Button delBtn = new Button("删除");
            private final HBox pane = new HBox(4, editBtn, delBtn);
            { pane.setAlignment(Pos.CENTER);
              editBtn.setStyle("-fx-background-color: #d48806; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 3 10; -fx-background-radius: 3;");
              delBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 3 10; -fx-background-radius: 3;"); }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Supplier s = getTableView().getItems().get(getIndex());
                editBtn.setOnAction(e -> showEditDialog(s));
                delBtn.setOnAction(e -> {
                    if (new Alert(Alert.AlertType.CONFIRMATION, "确认删除「" + s.getName() + "」？").showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        supplierService.delete(s.getId());
                        refresh();
                    }
                });
                setGraphic(pane);
            }
        });

        table.getColumns().addAll(idCol, nameCol, contactCol, phoneCol, addressCol, actionCol);
        table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);

        refreshBtn.setOnAction(e -> refresh());
        addBtn.setOnAction(e -> showEditDialog(null));

        getChildren().addAll(title, toolbar, table);
        refresh();
    }

    private void refresh() { data.setAll(supplierService.getAll()); }

    private void showEditDialog(Supplier supplier) {
        boolean isNew = supplier == null;
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(isNew ? "新增供应商" : "编辑供应商");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setPadding(new Insets(20));

        TextField nameField = new TextField(isNew ? "" : supplier.getName());
        TextField contactField = new TextField(isNew ? "" : supplier.getContactPerson());
        TextField phoneField = new TextField(isNew ? "" : supplier.getPhone());
        TextField addressField = new TextField(isNew ? "" : supplier.getAddress());

        form.add(new Label("名称:"), 0, 0); form.add(nameField, 1, 0);
        form.add(new Label("联系人:"), 0, 1); form.add(contactField, 1, 1);
        form.add(new Label("电话:"), 0, 2); form.add(phoneField, 1, 2);
        form.add(new Label("地址:"), 0, 3); form.add(addressField, 1, 3);

        Button saveBtn = new Button("保存");
        saveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            Supplier s = isNew ? new Supplier() : supplier;
            s.setName(nameField.getText());
            s.setContactPerson(contactField.getText());
            s.setPhone(phoneField.getText());
            s.setAddress(addressField.getText());
            if (isNew) supplierService.add(s);
            else supplierService.update(s);
            refresh(); dialog.close();
        });

        Button cancelBtn = new Button("取消");
        cancelBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dialog.close());

        VBox root = new VBox(10, form, new HBox(10, saveBtn, cancelBtn));
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
