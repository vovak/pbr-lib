package astminer.examples;

import astminer.common.model.LabeledResult;
import astminer.common.model.*;
import astminer.parse.gumtree.java.GumTreeJavaParser;
import astminer.storage.path.Code2VecPathStorage;
import astminer.storage.path.PathBasedStorage;
import astminer.storage.path.PathBasedStorageConfig;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

//Retrieve paths from Java files, using a GumTree parser.
public class AllJavaFiles {
    private static final String INPUT_FOLDER = "src/test/resources/gumTreeMethodSplitter";
    private static final String OUTPUT_FOLDER = "out_examples/allJavaFiles_GumTree_java";

    public static void runExample() {
        final PathBasedStorageConfig config = new PathBasedStorageConfig(5, 5, null, null, null);
        final PathBasedStorage pathStorage = new Code2VecPathStorage(OUTPUT_FOLDER, config);

        final Path inputFolder = Paths.get(INPUT_FOLDER);

        FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                Node fileTree = new GumTreeJavaParser().parseInputStream(new FileInputStream(file.toFile()));

                String filePath = file.toAbsolutePath().toString();
                pathStorage.store(new LabeledResult<>(fileTree, filePath, filePath));

                return FileVisitResult.CONTINUE;
            }
        };

        try {
            Files.walkFileTree(inputFolder, fileVisitor);
            pathStorage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
