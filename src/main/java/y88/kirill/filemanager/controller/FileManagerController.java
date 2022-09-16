package y88.kirill.filemanager.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import y88.kirill.filemanager.FileInfo;
import y88.kirill.filemanager.handler.FileHandler;
import y88.kirill.filemanager.handler.FileHandlerImpl;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;


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
        FileInfo fi = fromPanel.getSelectedFileInfo();
        String fileName = fi.getFilename();
        String question = String.format("Вы уверены что хотите переместить %s?",fileName);
        Alert alertQuestion = new Alert(Alert.AlertType.NONE, question, ButtonType.YES, ButtonType.CANCEL );
        Optional<ButtonType> result = alertQuestion.showAndWait();
        if(result.get() == ButtonType.YES) {
            try {
                Path toPath = Paths.get(toPanel.pathField.getText());
                fileHandler.move(fi, toPath);
                toPanel.updateList(toPath);
                fromPanel.updateList(Paths.get(fromPanel.pathField.getText()));
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось переместить указанный файл", ButtonType.OK);
                alert.showAndWait();
            }
        }else if(result.get() == ButtonType.CANCEL){
            alertQuestion.close();
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
                fileHandler.delete(fi);
                fromPanel.updateList(Paths.get(fromPanel.pathField.getText()));
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось удалить указанный файл", ButtonType.OK);
                alert.showAndWait();
            }
        }else if(result.get() == ButtonType.CANCEL){
            alertQuestion.close();
        }
    }

    public void renameBtn(ActionEvent actionEvent) {
        selectPC();
        FileInfo fi = fromPanel.getSelectedFileInfo();
        String newName;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Переименование файла/каталога");
        dialog.setHeaderText("Введите новое имя файла/каталога");
        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()){
            newName = result.get();
            try {
                fileHandler.rename(fi, newName);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось переименовать файл/каталог", ButtonType.OK);
                alert.showAndWait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                fromPanel.updateList(Paths.get(fromPanel.pathField.getText()));
            }
        }
    }

    public void createBtn(ActionEvent actionEvent) {
        focusTable();
        Path pathTo = Path.of(fromPanel.pathField.getText());
        String nameCatalog;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Создание каталога");
        dialog.setHeaderText("Введите новое имя каталога");
        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()){
            nameCatalog = result.get();
            try {
                fileHandler.createDir(pathTo, nameCatalog);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось создать каталог", ButtonType.OK);
                alert.showAndWait();
            }  finally {
                fromPanel.updateList(Paths.get(fromPanel.pathField.getText()));
            }
        }
    }

    public void updateBtn(ActionEvent actionEvent) {
        leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
        rightPC = (PanelController) rightPanel.getProperties().get("ctrl");
        leftPC.updateList(Paths.get(leftPC.pathField.getText()));
        rightPC.updateList(Paths.get(rightPC.pathField.getText()));
    }

    public void searchBtn(ActionEvent actionEvent) {
        focusTable();
        Path pathTo = Path.of(fromPanel.getSelectedFileInfo().getPath().toString());
        String nameSearch;
        List<Path> findPaths;
        StringBuilder sb = new StringBuilder();
        String findPathsNames = "";

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Поиск");
        dialog.setHeaderText("Введите значение для поиска (к сожалению не зависает только на неглубоких каталогах)");
        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()) {
            nameSearch = result.get();

            try {
                findPaths = fileHandler.find(pathTo, nameSearch);
                for (Path p: findPaths ) {
                    sb.append(p.toString()).append("\n");
                }
                Alert alert = new Alert(Alert.AlertType.NONE, sb.toString(), ButtonType.FINISH );
                Optional<ButtonType> resultAlert = alert.showAndWait();
                if(resultAlert.get() == ButtonType.FINISH){
                    alert.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


/**
 * Метод определяет активную панель с наличием выбранного элемента
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

/**
  * Метод определяет активную таблицу
  * */
    private void focusTable(){
        leftPC = (PanelController) leftPanel.getProperties().get("ctrl");
        rightPC = (PanelController) rightPanel.getProperties().get("ctrl");

        if(!leftPC.table.isFocused()&& !rightPC.table.isFocused()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Директория не была выбрана", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        if (leftPC.table.isFocused()) {
            fromPanel = leftPC;
            toPanel = rightPC;
        } else if(rightPC.table.isFocused()){
            fromPanel = rightPC;
            toPanel = leftPC;
        }
    }



}
