package cs.sonu.personalAssiatnt.storage;

import cs.sonu.personalAssiatnt.model.DocumentInfo;

import java.nio.file.Path;
import java.util.List;

public interface DocumentStorage {

    List<DocumentInfo> listDocuments(String folderName);

    DocumentInfo findDocument(String documentName);

    Path getDocumentPath(String documentId);

    List<DocumentInfo> listAllDocuments();

    List<String> listFolders();

    void createFolder(String folderName);

    void saveDocument(String folderName, org.springframework.web.multipart.MultipartFile file);
}