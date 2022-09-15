package y88.kirill.filemanager.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileHandler <T>{

    List<T> viewDirectory(Path path) throws IOException;
    void delete (T t) throws IOException;
    T rename(T t, String newName);
    T copy(T t, Path path) throws IOException;
    T move(T t, Path path) throws IOException;
    boolean createDir(Path path, String name);

    Path find(String name);


}
