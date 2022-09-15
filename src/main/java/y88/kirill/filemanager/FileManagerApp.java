package y88.kirill.filemanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FileManagerApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(FileManagerApp.class.getResource("WindowFileManager.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("File manager");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}
