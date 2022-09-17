package y88.kirill.filemanager;

import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import y88.kirill.filemanager.handler.FileHandler;
import y88.kirill.filemanager.handler.FileHandlerImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileHandlerTest {

    private static FileHandler fileHandler;


    @BeforeAll
    public void init(){
        fileHandler = new FileHandlerImpl();
        try {
            Files.createFile(Paths.get("C:\\Main\\Education\\GB\\FileManager\\src\\test\\resources\\temp3\\f1.txt"));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @DisplayName("Create directory test")
    @Order(1)
    @ParameterizedTest
    @CsvSource({"C:\\Main\\Education\\GB\\FileManager\\src\\test\\resources\\temp3, dir1 ",
    "C:\\Main\\Education\\GB\\FileManager\\src\\test\\resources\\temp3, dir 2",})
    public void createDirTest(String path, String nameDir){

        try {
            fileHandler.createDir(Paths.get(path), nameDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(Files.isDirectory(Paths.get(path).resolve(nameDir)));

    }

    @DisplayName("Create directory test exception")
    @Order(2)
    @ParameterizedTest
    @CsvSource({"C:\\Main\\Education\\GB\\FileManager\\src\\test\\resources\\temp3, dir1 ",
            "C:\\Main\\Education\\GB\\FileManager\\src\\test\\resources\\temp3, dir 2",})
    public void createDirTestException(String path, String nameDir){

        assertTrue(Files.isDirectory(Paths.get(path).resolve(nameDir)));
        assertThrows(IOException.class, () -> {fileHandler.createDir(Paths.get(path), nameDir);});

    }

    @DisplayName("Rename file test")
    @Order(3)
    @ParameterizedTest
    @CsvSource({"C:\\Main\\Education\\GB\\FileManager\\src\\test\\resources\\temp3\\f1.txt, f2.txt "})
    public void renameFileTest(String path, String nameFile){

        try {
            fileHandler.rename(new FileInfo(Paths.get( path)),nameFile);
            assertFalse(Files.isDirectory(Paths.get(path).resolve(nameFile)));


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }




    @AfterAll
    public void end(){
        try {
            Files.delete(Paths.get("C:\\Main\\Education\\GB\\FileManager\\src\\test\\resources\\temp3").resolve("dir1"));
            Files.delete(Paths.get("C:\\Main\\Education\\GB\\FileManager\\src\\test\\resources\\temp3").resolve("dir 2"));
            Files.delete(Paths.get("C:\\Main\\Education\\GB\\FileManager\\src\\test\\resources\\temp3\\f2.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





}
