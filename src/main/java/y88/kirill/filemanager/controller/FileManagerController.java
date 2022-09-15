package y88.kirill.filemanager.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.kordamp.bootstrapfx.scene.layout.Panel;
import y88.kirill.filemanager.FileInfo;
import y88.kirill.filemanager.handler.FileHandler;
import y88.kirill.filemanager.handler.FileHandlerImpl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class FileManagerController {

    private final FileHandler<FileInfo> fileHandler;

    @FXML
    VBox leftPanel, rightPanel;

    private PanelController leftPC = null;
    private PanelController rightPC = null;
    private PanelController fromPanel = null;
    private PanelController toPanel = null;



    public FileManagerController() {
        this.fileHandler = new FileHandlerImpl();
    }

    public void copyBtn(ActionEvent actionEvent) {
        selectPC();
        try {
            Path toPath = Paths.get(toPanel.pathField.getText());
            fileHandler.copy(fromPanel.getSelectedFileInfo(), toPath);
            toPanel.updateList(toPath);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось скопировать указанный файл", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void moveBtn(){
        selectPC();
        try {
            Path toPath = Paths.get(toPanel.pathField.getText());
            fileHandler.move(fromPanel.getSelectedFileInfo(), toPath);
            toPanel.updateList(toPath);
            fromPanel.updateList(Paths.get(fromPanel.pathField.getText()));
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось переместить указанный файл", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void deleteBtn(ActionEvent actionEvent) {
        selectPC();
        FileInfo fi = fromPanel.getSelectedFileInfo();
        String fileName = fi.getFilename();
        String question = String.format("Вы уверены что хотите удалить %s?",fileName);
        Alert alertQuestion = new Alert(Alert.AlertType.NONE, question, ButtonType.YES, ButtonType.CANCEL );
        Optional<ButtonType> result = alertQuestion.showAndWait();
        if(result.get() == ButtonType.YES){
            try {
                if(fi.getType() == FileInfo.FType.FILE){
                fileHandler.delete(fi);
                fromPanel.updateList(Paths.get(fromPanel.pathField.getText()));
                }else {
                    //удаление каталога с файлами
                    Files.walk(fi.getPath())
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                    fromPanel.updateList(Paths.get(fromPanel.pathField.getText()));
                }
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось удалить указанный файл", ButtonType.OK);
                alert.showAndWait();
            }


        }else if(result.get() == ButtonType.CANCEL){
            alertQuestion.close();
        }



    }


/**
 * Метод определяет активную панель
 * */
    private void selectPC() {
        leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
        rightPC = (PanelController) rightPanel.getProperties().get("ctrl");

        if(leftPC.getSelectedFileInfo() == null && rightPC.getSelectedFileInfo() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Файл не был выбран", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        if (leftPC.getSelectedFileInfo() != null){
            fromPanel = leftPC;
            toPanel = rightPC;
        }else if (rightPC.getSelectedFileInfo() != null){
            fromPanel = rightPC;
            toPanel = leftPC;
        }
    }



}
