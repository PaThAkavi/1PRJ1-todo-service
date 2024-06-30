package util;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileConstants {
    public static final String FS = File.separator;
    public static final Charset charset = StandardCharsets.UTF_8;
    public static final String GENERATED_SRC_MAIN_FOLDER = "build/generated-src/main/".replace("/", FS);
    public static final String GENERATED_SRC_JAVA_FOLDER = GENERATED_SRC_MAIN_FOLDER + "java" + FS;
    public static final String GENERATED_SRC_RESOURCES_FOLDER = GENERATED_SRC_MAIN_FOLDER + "resources" + FS;
    public static final String RESOURCES_FOLDER = "/src/main/resources".replace("/", FS);
}