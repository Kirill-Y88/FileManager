package y88.kirill.filemanager.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import y88.kirill.filemanager.FileInfo;
import y88.kirill.filemanager.handler.FileHandler;
import y88.kirill.filemanager.handler.FileHandlerImpl;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PanelController implements Initializable {

    private final FileHandler<FileInfo> fileHandler;

    @FXML
    TableView<FileInfo> table;

    @FXML
    TextField pathField;

    @FXML
    ComboBox disk;

    @FXML
    Button btnUp;

    public PanelController() {
        fileHandler = new FileHandlerImpl();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>(); // создание столбца
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        //перевод из типа файл инфо в стринг
        fileTypeColumn.setPrefWidth(60);

        TableColumn<FileInfo, String> filenameColumn = new TableColumn<>("Имя");
        filenameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        filenameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }
            };
        });
        fileSizeColumn.setPrefWidth(100);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Дата изменения");
        fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDateColumn.setPrefWidth(120);

        table.getColumns().addAll(fileTypeColumn, filenameColumn, fileSizeColumn, fileDateColumn); //добавление созданных столбцов
        table.getSortOrder().add(fileTypeColumn);

        disk.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            disk.getItems().add(p.toString());
        }
        disk.getSelectionModel().select(0);


        table.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Path path = Paths.get(pathField.getText()).resolve(table.getSelectionModel().getSelectedItem().getFilename());
                    if (Files.isDirectory(path)) {
                        updateList(path);
                    }
                }
            }
        });

        updateList(FileSystems.getDefault().getRootDirectories().iterator().next());
    }

    public void updateList(Path path) {
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());
            table.getItems().clear();
            table.getItems().addAll(fileHandler.viewDirectory(path));
            //запускаем стрим по указанному пути/преобразуем все полученное в объекты файл инфо/ упаковвываем полученные данные в коллекцию Лист
            table.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }
    }




    public void BtnUp(ActionEvent actionEvent) {
       Path path = Paths.get(pathField.getText()).getParent();
         if(path != null){
             updateList(path);
         }
    }


    public void selectDisk(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        updateList(Paths.get(element.getSelectionModel().getSelectedItem()));
    }


    public FileInfo getSelectedFileInfo(){
        if(!table.isFocused()){
            return null;
        }
        return table.getSelectionModel().getSelectedItem();
    }


}
