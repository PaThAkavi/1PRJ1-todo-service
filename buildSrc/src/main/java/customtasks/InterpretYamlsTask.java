package customtasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.impldep.org.apache.commons.lang.StringUtils;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;
import util.TaskUtils;

import jakarta.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.io.FileUtils.iterateFiles;
import static org.gradle.internal.FileUtils.removeExtension;
import static util.FileConstants.*;

public class InterpretYamlsTask extends DefaultTask {
    private final TaskUtils t;
    private static final String DOTTED_PROJECT_PACKAGE = "models.yamls";
    private static String YAML_INPUT_FOLDER = RESOURCES_FOLDER + ("apis/yamls/").replace("/", FS);

    @Inject
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
            String extensionless = removeExtension(file.getName()).replace("-", "_");
            String extendedProjectPackage = DOTTED_PROJECT_PACKAGE + "._" + extensionless;
            generatedModelClasses(swaggerSourceFile, extendedProjectPackage);
            cleanupUnusedOpenApiFiles();
            fixModels(extendedProjectPackage);
        }
    }

    private void generatedModelClasses(String swaggerSourceFile, String extendedProjectPackage) {
        CodegenConfigurator config = new CodegenConfigurator();
        config.setInputSpec(t.getFullPath(swaggerSourceFile).toString());
        config.setOutputDir(t.getProjDir());
        config.setGeneratorName("spring");
        config.addAdditionalProperty("modelPackage", extendedProjectPackage);
        config.addAdditionalProperty("sourceFolder", GENERATED_SRC_JAVA_FOLDER);
        config.addAdditionalProperty("serializableModel", true);
        config.addAdditionalProperty("openApiNullable", false);
        config.addAdditionalProperty("booleanGetterPrefix", "is");
        config.addAdditionalProperty("dateLibrary", "java17");

        new DefaultGenerator().opts(config.toClientOptInput()).generate();
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

    private void fixModels(String extendedProjectPackage) {
        File modelDirectory = new File(t.getProjDir() + GENERATED_SRC_JAVA_FOLDER + extendedProjectPackage.replace(".", FS));
        Iterator<File> files = iterateFiles(modelDirectory, null, false);
        while (files.hasNext()) {
            File modelFile = files.next();
            if (isAdsFrameworkClass(modelFile)) {
                deleteQuietly(modelFile);
            } else {
                editFileContent(modelFile, extendedProjectPackage);
            }
        }
    }

    private boolean isAdsFrameworkClass(File modelFile) {
        String[] adsFrameworkClasses = {
                "GetConnectionStatusRequests",
                "GetConnectionStatusResponse"
        };
        String extensionless = removeExtension(modelFile.getName());
        for (String frameworkClass : adsFrameworkClasses) {
            if (frameworkClass.equals(extensionless)) {
                return true;
            }
        }
        return false;
    }

    private void editFileContent(File modelFile, String extendedProjectPackage) {
        try {
            Path path = modelFile.toPath();
            String content = new String(Files.readAllBytes(path), charset);

            content = addCamelCaseFormating(content);
            content = editAccessors(content);

            content = content.replaceAll("\\n.*JsonTypeInfo.As.WRAPPER_OBJECT[^\\n]*", "");

            String frameworkModelPackage = "com.projects.todo.framework.model";
            content = content.replace(extendedProjectPackage + ".SystemErrorDiagnostics", frameworkModelPackage + ".SystemErrorDiagnostics");
            content = content.replace(extendedProjectPackage + ".SystemErrorResponse", frameworkModelPackage + ".SystemErrorResponse");
            content = content.replace(extendedProjectPackage + ".StatusMessage", frameworkModelPackage + ".StatusMessage");
            content = content.replace(extendedProjectPackage + ".SystemError", frameworkModelPackage + ".SystemError");

            Files.write(path, content.getBytes(charset));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String addCamelCaseFormating(String content) {
        Matcher m = Pattern.compile("(\\n.*)TO_CAMEL_CASE<(\\w)(\\w+)>([^\\n]*)").matcher(content);
        String ignoredSubClasses = "String,Boolean,Byte,Character,Float,Double,Long,Short";
        while (m.find()) {
            String replacement;
            if (ignoredSubClasses.contains(m.group(2) + m.group(3))) {
                replacement = "";
            } else {
                String lowerFirst = m.group(2).toLowerCase();
                replacement = m.group(1) + lowerFirst + m.group(3) + m.group(4);
            }
            content = content.replace(m.group(), replacement);
        }
        return content;
    }

    private String editAccessors(String content) {
        ArrayList<String> fields = getFields(content);

        for (String field: fields) {
            String capitalizedFields = StringUtils.capitalise(field);
            content = content.replace("is" + field, "is" + capitalizedFields);
            content = content.replace("et" + field, "et" + capitalizedFields);
        }
        return content;
    }

    private ArrayList<String> getFields(String content) {
        ArrayList<String> fields = new ArrayList<>();
        Matcher fieldMatcher = Pattern.compile("private \\w+ (\\w+);").matcher(content);
        while (fieldMatcher.find()) {
            fields.add(fieldMatcher.group(1));
        }
        return fields;
    }
}