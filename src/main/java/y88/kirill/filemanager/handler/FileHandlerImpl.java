package y88.kirill.filemanager.handler;

import y88.kirill.filemanager.FileInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

public class FileHandlerImpl implements FileHandler<FileInfo>{

    @Override
    public List<FileInfo> viewDirectory(Path path) throws IOException {
       return Files.list(path)
               .map(FileInfo::new)
               .collect(Collectors.toList());
    }

    @Override
    public void delete(FileInfo o) throws IOException {


        Files.delete(o.getPath());



    }

    @Override
    public FileInfo rename(FileInfo o, String newName) {
        return null;
    }

    @Override
    public FileInfo copy(FileInfo o, Path path) throws IOException {

        Path toPath = path;
        Path fromPath = o.getPath();

        if(o.getType()== FileInfo.FType.FILE){
            Files.copy(o.getPath(),  path.resolve(o.getFilename()));
        } else {
            toPath = toPath.resolve(o.getFilename());
            Path finalToPath = toPath;
            Files.walk(fromPath)
                    .forEach(source -> {
                        try {
                            Files.copy(source, finalToPath.resolve(fromPath.relativize(source)),
                                    StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
        FileInfo fi = new FileInfo(path.resolve(o.getFilename()));
        return fi;
    }

    @Override
    public FileInfo move(FileInfo o, Path path) throws IOException {
        Files.move(o.getPath(), path.resolve(o.getFilename()));
        FileInfo fi = new FileInfo(path.resolve(o.getFilename()));
        return fi;
    }

    @Override
    public boolean createDir(Path path, String name) {
        return false;
    }

    @Override
    public Path find(String name) {
        return null;
    }
}
