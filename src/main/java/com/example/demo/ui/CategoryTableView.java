// 这是一个管理分类的界面，包含了增删改查的业务共能
package com.example.demo.ui;
// 引入javafx的包
// com.example.demo.config.SpringContextholder 是一个工具类，
// 用于在非Spring管理的类中获取Spring Bean实例
// 是自己命名的，放在了 com.example.demo.config 包下
// springContexHolder 的实现很简单，
// 就是通过静态变量持有 Spring 的 ApplicationContext
import com.example.demo.config.SpringContextHolder;
import com.example.demo.entity.Category;
import com.example.demo.service.CategoryService;
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

public class CategoryTableView extends VBox {

    private final CategoryService categoryService = SpringContextHolder.getBean(CategoryService.class);
    private final TableView<Category> table = new TableView<>();
    private final ObservableList<Category> data = FXCollections.observableArrayList();

    public CategoryTableView() {
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("📂 分类管理");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 22));

        HBox toolbar = new HBox(10);
        Button addBtn = new Button("➕ 新增分类");
        Button refreshBtn = new Button("🔄 刷新");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 13; -fx-padding: 8 16;");
        refreshBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 13; -fx-padding: 8 16;");
        toolbar.getChildren().addAll(addBtn, refreshBtn);

        TableColumn<Category, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<Category, String> nameCol = new TableColumn<>("分类名称");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Category, String> descCol = new TableColumn<>("描述");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(300);

        TableColumn<Category, Void> actionCol = new TableColumn<>("操作");
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
                Category c = getTableView().getItems().get(getIndex());
                editBtn.setOnAction(e -> showEditDialog(c));
                delBtn.setOnAction(e -> {
                    if (new Alert(Alert.AlertType.CONFIRMATION, "确认删除「" + c.getName() + "」？").showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        categoryService.delete(c.getId());
                        refresh();
                    }
                });
                setGraphic(pane);
            }
        });

        table.getColumns().addAll(idCol, nameCol, descCol, actionCol);
        table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);

        refreshBtn.setOnAction(e -> refresh());
        addBtn.setOnAction(e -> showEditDialog(null));

        getChildren().addAll(title, toolbar, table);
        refresh();
    }

    private void refresh() { data.setAll(categoryService.getAll()); }

    private void showEditDialog(Category category) {
        boolean isNew = category == null;
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(isNew ? "新增分类" : "编辑分类");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setPadding(new Insets(20));

        TextField nameField = new TextField(isNew ? "" : category.getName());
        TextArea descArea = new TextArea(isNew ? "" : category.getDescription());
        descArea.setPrefRowCount(3);

        form.add(new Label("分类名称:"), 0, 0); form.add(nameField, 1, 0);
        form.add(new Label("描述:"), 0, 1); form.add(descArea, 1, 1);

        Button saveBtn = new Button("保存");
        saveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            Category c = isNew ? new Category() : category;
            c.setName(nameField.getText());
            c.setDescription(descArea.getText());
            if (isNew) categoryService.add(c);
            else categoryService.update(c);
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
