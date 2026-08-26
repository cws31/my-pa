package cs.sonu.personalAssiatnt.storage;

import cs.sonu.personalAssiatnt.model.DocumentInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Component
public class FileSystemDocumentStorage implements DocumentStorage {

    private final Path rootPath;

    public FileSystemDocumentStorage(
            @Value("${documents.root-path}") String rootPath) {

        this.rootPath = Path.of(rootPath)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public List<DocumentInfo> listDocuments(String folderName) {

        Path folderPath = rootPath.resolve(folderName).normalize();

        validatePathInsideRoot(folderPath);

        if (!Files.isDirectory(folderPath)) {
            return List.of();
        }

        try (Stream<Path> paths = Files.list(folderPath)) {

            return paths
                    .filter(Files::isRegularFile)
                    .map(this::toDocumentInfo)
                    .toList();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to list documents from folder: " + folderName,
                    e);
        }
    }

    @Override
    public DocumentInfo findDocument(String documentName) {
        try (Stream<Path> paths = Files.walk(rootPath)) {
            String query = documentName.toLowerCase().replaceAll("[^a-z0-9]", "");

            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String fileName = path.getFileName().toString().toLowerCase();
                        String cleanFileName = fileName.replaceAll("[^a-z0-9]", "");

                        return cleanFileName.contains(query) || query.contains(cleanFileName);
                    })
                    .findFirst()
                    .map(this::toDocumentInfo)
                    .orElse(null);

        } catch (IOException e) {
            throw new IllegalStateException("Unable to search documents", e);
        }
    }

    private DocumentInfo toDocumentInfo(Path path) {

        Path relativePath = rootPath.relativize(path);

        String fileName = path.getFileName().toString();

        String folderName = relativePath.getNameCount() > 1
                ? relativePath.getName(0).toString()
                : "";

        String type = getFileExtension(fileName);

        String id = relativePath
                .toString()
                .replace("\\", "/");

        return new DocumentInfo(
                id,
                fileName,
                folderName,
                type);
    }

    private String getFileExtension(String fileName) {

        int index = fileName.lastIndexOf('.');

        if (index == -1) {
            return "UNKNOWN";
        }

        return fileName.substring(index + 1).toUpperCase();
    }

    private void validatePathInsideRoot(Path path) {

        if (!path.startsWith(rootPath)) {
            throw new IllegalArgumentException(
                    "Invalid document path");
        }
    }

    @Override
    public Path getDocumentPath(String documentId) {

        Path documentPath = rootPath
                .resolve(documentId)
                .normalize();

        if (!documentPath.startsWith(rootPath)) {
            throw new IllegalArgumentException(
                    "Invalid document path");
        }

        if (!Files.isRegularFile(documentPath)) {
            throw new IllegalArgumentException(
                    "Document not found: " + documentId);
        }

        return documentPath;
    }

    @Override
    public List<DocumentInfo> listAllDocuments() {
        try (Stream<Path> paths = Files.walk(rootPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(this::toDocumentInfo)
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to list all documents", e);
        }
    }

    @Override
    public List<String> listFolders() {
        try (Stream<Path> paths = Files.list(rootPath)) {
            return paths
                    .filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to list folders", e);
        }
    }
}