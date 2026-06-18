package com.example.demo.ui;

import com.example.demo.config.SpringContextHolder;
import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
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

import java.time.LocalDateTime;

public class CustomerTableView extends VBox {

    private final CustomerService customerService = SpringContextHolder.getBean(CustomerService.class);
    private final TableView<Customer> table = new TableView<>();
    private final ObservableList<Customer> data = FXCollections.observableArrayList();

    public CustomerTableView() {
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("👥 客户管理");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 22));

        HBox toolbar = new HBox(10);
        Button addBtn = new Button("➕ 新增客户");
        Button refreshBtn = new Button("🔄 刷新");
        addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        toolbar.getChildren().addAll(addBtn, refreshBtn);

        TableColumn<Customer, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Customer, String> nameCol = new TableColumn<>("姓名");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(100);

        TableColumn<Customer, String> phoneCol = new TableColumn<>("电话");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(120);

        TableColumn<Customer, String> addressCol = new TableColumn<>("地址");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressCol.setPrefWidth(200);

        TableColumn<Customer, String> emailCol = new TableColumn<>("邮箱");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(180);

        TableColumn<Customer, String> roleCol = new TableColumn<>("角色");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(80);

        TableColumn<Customer, LocalDateTime> timeCol = new TableColumn<>("创建时间");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        timeCol.setPrefWidth(150);

        TableColumn<Customer, Void> actionCol = new TableColumn<>("操作");
        actionCol.setPrefWidth(150);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("编辑");
            private final Button delBtn = new Button("删除");
            private final HBox pane = new HBox(5, editBtn, delBtn);
            {
                pane.setAlignment(Pos.CENTER);
                editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand;");
                delBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Customer c = getTableView().getItems().get(getIndex());
                editBtn.setOnAction(e -> showEditDialog(c));
                delBtn.setOnAction(e -> {
                    if (new Alert(Alert.AlertType.CONFIRMATION, "确认删除「" + c.getName() + "」？").showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        customerService.delete(c.getId());
                        refresh();
                    }
                });
                setGraphic(pane);
            }
        });

        table.getColumns().addAll(idCol, nameCol, phoneCol, addressCol, emailCol, roleCol, timeCol, actionCol);
        table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);

        refreshBtn.setOnAction(e -> refresh());
        addBtn.setOnAction(e -> showEditDialog(null));

        getChildren().addAll(title, toolbar, table);
        refresh();
    }

    private void refresh() { data.setAll(customerService.getAll()); }

    private void showEditDialog(Customer customer) {
        boolean isNew = customer == null;
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(isNew ? "新增客户" : "编辑客户");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setPadding(new Insets(20));

        TextField nameField = new TextField(isNew ? "" : customer.getName());
        TextField phoneField = new TextField(isNew ? "" : customer.getPhone());
        TextField addressField = new TextField(isNew ? "" : customer.getAddress());
        TextField emailField = new TextField(isNew ? "" : customer.getEmail());
        TextField passwordField = new TextField(isNew ? "123456" : customer.getPassword());
        ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList("customer", "staff"));
        roleCombo.setValue(isNew ? "customer" : customer.getRole());

        form.add(new Label("姓名:"), 0, 0); form.add(nameField, 1, 0);
        form.add(new Label("电话:"), 0, 1); form.add(phoneField, 1, 1);
        form.add(new Label("地址:"), 0, 2); form.add(addressField, 1, 2);
        form.add(new Label("邮箱:"), 0, 3); form.add(emailField, 1, 3);
        form.add(new Label("密码:"), 0, 4); form.add(passwordField, 1, 4);
        form.add(new Label("角色:"), 0, 5); form.add(roleCombo, 1, 5);

        Button saveBtn = new Button("保存");
        saveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            try {
                Customer c = isNew ? new Customer() : customer;
                c.setName(nameField.getText());
                c.setPhone(phoneField.getText());
                c.setAddress(addressField.getText());
                c.setEmail(emailField.getText());
                c.setPassword(passwordField.getText());
                c.setRole(roleCombo.getValue());
                if (isNew) customerService.add(c);
                else customerService.update(c);
                refresh(); dialog.close();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "输入有误: " + ex.getMessage()).show();
            }
        });

        Button cancelBtn = new Button("取消");
        cancelBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dialog.close());

        VBox root = new VBox(10, form, new HBox(10, saveBtn, cancelBtn));
        root.setPadding(new Insets(10));
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }
}
