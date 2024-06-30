package customtasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import util.TaskUtils;

//import jakarta.inject.Inject;

import java.io.File;

import static util.FileConstants.*;

public class InterpretYamlsTask extends DefaultTask {
    private final TaskUtils t;
    private static final String DOTTED_PROJECT_PACKAGE = "models.yamls";
    private static String YAML_INPUT_FOLDER = RESOURCES_FOLDER + ("apis/yamls/").replace("/", FS);

//    @Inject
    public InterpretYamlsTask(TaskUtils t) {
        this.t = new TaskUtils(this);
    }

    @InputDirectory
    @SkipWhenEmpty
    public File getInputDirectory() { return new File(t.getProjDir() + YAML_INPUT_FOLDER); }

    @OutputDirectory
    public File getOutputDirectory() { return new File(t.getProjDir() + GENERATED_SRC_JAVA_FOLDER + DOTTED_PROJECT_PACKAGE.replace(".", FS)); }

    @TaskAction
    public void runTask() {
        for (File file : getInputs().getFiles()) {
            String swaggerSourceFile = YAML_INPUT_FOLDER + file.getName();
//            String extensionless = removeExtension(file.getName()).replace("-", "_");
//            String extendedProjectPackage = DOTTED_PROJECT_PACKAGE + "._" + extensionless;
//            generatedModelClasss(swaggerSourceFile, extendedProjectPackage);
            cleanupUnusedOpenApiFiles();
//            fixModels(extendedProjectPackage);
        }
    }

    private void generatedModelClasses(String swaggerSourceFile, String extendedProjectPackage) {

    }

    private void cleanupUnusedOpenApiFiles() {
        t.deleteQuietly(".openapi-generator",
                GENERATED_SRC_JAVA_FOLDER + "org",
                ".openapi-generator-ignore",
                "pom.xml",
                "README.md",
                RESOURCES_FOLDER + "application.properties"
        );
    }

}