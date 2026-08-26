package cs.sonu.personalAssiatnt.controller;

import cs.sonu.personalAssiatnt.service.DocumentService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/documents")

public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity<Resource> openDocument(@RequestParam String id) {
        try {
            Path filePath = documentService.getDocumentPath(id);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Could not read file: " + id);
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Error opening document: " + e.getMessage(), e);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(
            @RequestParam String folderName,
            @RequestParam("file") MultipartFile file) {
        documentService.saveDocument(folderName, file);
        return ResponseEntity.ok("File uploaded successfully to " + folderName);
    }

    @PostMapping("/folders")
    public ResponseEntity<String> createFolder(@RequestParam String folderName) {
        documentService.createFolder(folderName);
        return ResponseEntity.ok("Folder created successfully: " + folderName);
    }
}