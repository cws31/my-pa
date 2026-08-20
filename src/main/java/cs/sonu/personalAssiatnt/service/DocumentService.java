package cs.sonu.personalAssiatnt.service;

import org.springframework.stereotype.Service;

import cs.sonu.personalAssiatnt.model.DocumentInfo;
import cs.sonu.personalAssiatnt.storage.DocumentStorage;

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
}