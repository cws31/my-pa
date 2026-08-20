package cs.sonu.personalAssiatnt.storage;

import org.springframework.stereotype.Component;

import cs.sonu.personalAssiatnt.model.DocumentInfo;

import java.util.List;

@Component
public class LocalDocumentStorage implements DocumentStorage {

    @Override
    public List<DocumentInfo> listDocuments(String folderName) {

        return List.of(
                new DocumentInfo(
                        "doc-001",
                        "10th marksheet.pdf",
                        folderName,
                        "PDF"),
                new DocumentInfo(
                        "doc-002",
                        "10th admit card.pdf",
                        folderName,
                        "PDF"));
    }

    @Override
    public DocumentInfo findDocument(String documentName) {

        return new DocumentInfo(
                "doc-001",
                documentName + ".pdf",
                "10th documents",
                "PDF");
    }
}