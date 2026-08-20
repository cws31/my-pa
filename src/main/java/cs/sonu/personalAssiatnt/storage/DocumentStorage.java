package cs.sonu.personalAssiatnt.storage;

import java.util.List;

import cs.sonu.personalAssiatnt.model.DocumentInfo;

public interface DocumentStorage {

    List<DocumentInfo> listDocuments(String folderName);

    DocumentInfo findDocument(String documentName);
}