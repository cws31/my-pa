package cs.sonu.personalAssiatnt.storage;

import cs.sonu.personalAssiatnt.model.DocumentInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

    private Path getCurrentUserRoot() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userEmail = "anonymous";
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser")) {
            userEmail = authentication.getName();
        }

        String safeUserDir = userEmail.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path userPath = rootPath.resolve(safeUserDir).normalize();

        try {
            Files.createDirectories(userPath);
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize user storage directory", e);
        }
        return userPath;
    }

    private void validatePathInsideUserRoot(Path path, Path userRoot) {
        if (!path.startsWith(userRoot)) {
            throw new IllegalArgumentException("Invalid document path access attempt");
        }
    }

    @Override
    public List<DocumentInfo> listDocuments(String folderName) {
        Path userRoot = getCurrentUserRoot();
        Path folderPath = userRoot.resolve(folderName).normalize();
        validatePathInsideUserRoot(folderPath, userRoot);

        if (!Files.isDirectory(folderPath)) {
            return List.of();
        }

        try (Stream<Path> paths = Files.list(folderPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> toDocumentInfo(userRoot, path))
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to list documents from folder: " + folderName, e);
        }
    }

    @Override
    public DocumentInfo findDocument(String documentName) {
        Path userRoot = getCurrentUserRoot();
        try (Stream<Path> paths = Files.walk(userRoot)) {
            String query = documentName.toLowerCase().replaceAll("[^a-z0-9]", "");

            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> toDocumentInfo(userRoot, path))
                    .filter(doc -> {

                        String cleanFileName = doc.name().toLowerCase().replaceAll("[^a-z0-9]", "");
                        return cleanFileName.contains(query) || query.contains(cleanFileName);
                    })
                    .findFirst()
                    .orElse(null);

        } catch (IOException e) {
            throw new IllegalStateException("Unable to search documents", e);
        }
    }

    @Override
    public Path getDocumentPath(String documentId) {
        Path userRoot = getCurrentUserRoot();
        Path documentPath = userRoot.resolve(documentId).normalize();
        validatePathInsideUserRoot(documentPath, userRoot);

        if (!Files.isRegularFile(documentPath)) {
            throw new IllegalArgumentException("Document not found: " + documentId);
        }

        return documentPath;
    }

    @Override
    public List<DocumentInfo> listAllDocuments() {
        Path userRoot = getCurrentUserRoot();
        try (Stream<Path> paths = Files.walk(userRoot)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(path -> toDocumentInfo(userRoot, path))
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to list all documents", e);
        }
    }

    @Override
    public List<String> listFolders() {
        Path userRoot = getCurrentUserRoot();
        try (Stream<Path> paths = Files.walk(userRoot)) {
            return paths
                    .filter(Files::isDirectory)
                    .filter(path -> !path.equals(userRoot))
                    .map(path -> userRoot.relativize(path).toString().replace("\\", "/"))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to list folders", e);
        }
    }

    @Override
    public void createFolder(String folderName) {
        Path userRoot = getCurrentUserRoot();
        Path folderPath = userRoot.resolve(folderName).normalize();
        validatePathInsideUserRoot(folderPath, userRoot);

        try {
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create folder: " + folderName, e);
        }
    }

    @Override
    public void saveDocument(String folderName, MultipartFile file) {
        Path userRoot = getCurrentUserRoot();
        Path folderPath = userRoot.resolve(folderName).normalize();
        validatePathInsideUserRoot(folderPath, userRoot);

        try {
            Files.createDirectories(folderPath);
            Path targetPath = folderPath.resolve(file.getOriginalFilename()).normalize();
            validatePathInsideUserRoot(targetPath, userRoot);

            file.transferTo(targetPath.toFile());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to save document", e);
        }
    }

    @Override
    public void deleteDocument(String documentId) {
        Path userRoot = getCurrentUserRoot();
        Path filePath = userRoot.resolve(documentId).normalize();
        validatePathInsideUserRoot(filePath, userRoot);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to delete document: " + documentId, e);
        }
    }

    @Override
    public void renameDocument(String documentId, String newName) {
        Path userRoot = getCurrentUserRoot();
        Path oldPath = userRoot.resolve(documentId).normalize();
        validatePathInsideUserRoot(oldPath, userRoot);

        if (!Files.exists(oldPath)) {
            throw new IllegalArgumentException("Document not found: " + documentId);
        }

        Path parentDir = oldPath.getParent();
        Path newPath = parentDir.resolve(newName).normalize();
        validatePathInsideUserRoot(newPath, userRoot);

        try {
            Files.move(oldPath, newPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to rename document", e);
        }
    }

    private DocumentInfo toDocumentInfo(Path userRoot, Path path) {
        Path relativePath = userRoot.relativize(path);
        String fileName = path.getFileName().toString();
        String folderName = relativePath.getNameCount() > 1
                ? relativePath.getName(0).toString()
                : "";
        String type = getFileExtension(fileName);
        String id = relativePath.toString().replace("\\", "/");

        return new DocumentInfo(id, fileName, folderName, type);
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return "UNKNOWN";
        }
        return fileName.substring(index + 1).toUpperCase();
    }
}