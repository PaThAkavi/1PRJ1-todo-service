package util;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static util.FileConstants.FS;

public class TaskUtils {
    private final Task t;
    public TaskUtils(Task t) {this.t = t;}
    public String getProjDir() { return t.getProject().getProjectDir().getPath() + FS;}

    public Path getFullPath(String inProjectPath) {
        String fullPath = getProjDir() + FS + inProjectPath;
        return Paths.get(fullPath);
    }

    public void deleteQuietly(String... inProjectFileLocations) {
        for (String loc : inProjectFileLocations) {
            FileUtils.deleteQuietly(new File(getProjDir() + FS + loc));
        }
    }

    public void walkFiles(File rootDirectory, Consumer<Path> functionThatTakesPath) {
        try (Stream<Path> walk = Files.walk(rootDirectory.toPath())) {
            List<Path> result = walk.filter(Files::isRegularFile).collect(Collectors.toList());
            result.forEach(functionThatTakesPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}