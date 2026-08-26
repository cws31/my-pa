package cs.sonu.personalAssiatnt.service;

import org.springframework.stereotype.Service;

import cs.sonu.personalAssiatnt.model.DocumentInfo;
import cs.sonu.personalAssiatnt.storage.DocumentStorage;

import java.nio.file.Path;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentStorage documentStorage;

    public DocumentService(DocumentStorage documentStorage) {
        this.documentStorage = documentStorage;
    }

    public List<DocumentInfo> listDocuments(String folderName) {
        return documentStorage.listDocuments(folderName);
    }

    public DocumentInfo findDocument(String documentName) {
        return documentStorage.findDocument(documentName);
    }

    public Path getDocumentPath(String documentId) {
        return documentStorage.getDocumentPath(documentId);
    }

    public List<DocumentInfo> listAllDocuments() {
        return documentStorage.listAllDocuments();
    }

    public List<String> listFolders() {
        return documentStorage.listFolders();
    }

    public void createFolder(String folderName) {
        documentStorage.createFolder(folderName);
    }

    public void saveDocument(String folderName, org.springframework.web.multipart.MultipartFile file) {
        documentStorage.saveDocument(folderName, file);
    }
}