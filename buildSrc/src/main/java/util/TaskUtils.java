package util;

import org.gradle.api.Task;

import java.nio.file.Path;
import java.nio.file.Paths;

import static util.FileConstants.FS;

public class TaskUtils {
    private final Task t;
    public TaskUtils(Task t) {this.t = t;}
    public String getProjDir() { return t.getProject().getProjectDir().getPath() + FS;}

    public Path geFullPath(String inProjectPath) {
        String fullPath = getProjDir() + FS + inProjectPath;
        return Paths.get(fullPath);
    }

    public void deleteQuietly(String... inProjectFileLocations) {
        for (String loc : inProjectFileLocations) {
//            FileUtils
        }
    }
}