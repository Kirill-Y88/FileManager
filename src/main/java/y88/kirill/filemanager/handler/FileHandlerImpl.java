package y88.kirill.filemanager.handler;

import y88.kirill.filemanager.FileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
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
        Path fromPath = o.getPath();
        if(o.getType() == FileInfo.FType.FILE) {
            Files.delete(fromPath);
        }else {
            Files.walk(fromPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Override
    public FileInfo rename(FileInfo o, String newName) throws IOException, InterruptedException {
        Path fromPath = o.getPath();
        Path toPath = fromPath.getParent().resolve(newName);
        System.out.println("fromPath:" + fromPath.toString());
        System.out.println("toPath:" + toPath.toString());
        String command = String.format("cmd /c ren \"%s\" \"%s\"", fromPath, newName);
        System.out.println(command);

        Runtime.getRuntime().exec(command);

        //немного паузы, иначе не создастся fileinfo
        Thread.sleep(150);

        return new FileInfo(toPath);
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
        return new FileInfo(path.resolve(o.getFilename()));
    }

    @Override
    public FileInfo move(FileInfo o, Path path) throws IOException {
        Path toPath = path;
        Path fromPath = o.getPath();
        if(o.getType() == FileInfo.FType.FILE){
            Files.move(fromPath, toPath.resolve(o.getFilename()));
        }else {
            copy(o, toPath);
            delete(o);
        }
        return new FileInfo(path.resolve(o.getFilename()));
    }

    @Override
    public void createDir(Path path, String name) throws IOException {
        Path newPath = path.resolve(name);
        Files.createDirectory(newPath);
    }

    @Override
    public  List<Path> find(Path path, String name) throws IOException {
        List<Path> findList = Files.walk(path)
                .filter( p -> {
                    return (p.toString().toLowerCase()).contains(name.toLowerCase());
                })
                .collect(Collectors.toList());
        return findList;
    }
}
