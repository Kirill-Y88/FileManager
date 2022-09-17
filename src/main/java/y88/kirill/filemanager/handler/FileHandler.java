package y88.kirill.filemanager.handler;

import y88.kirill.filemanager.FileInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileHandler <T extends FileInfo>{

    List<T> viewDirectory(Path path) throws IOException;
    void delete (T t) throws IOException;
    T rename(T t, String newName) throws IOException, InterruptedException;
    T copy(T t, Path path) throws IOException;
    T move(T t, Path path) throws IOException;
    void createDir(Path path, String name) throws IOException;

   List <Path> find(Path path, String name) throws IOException;
}
