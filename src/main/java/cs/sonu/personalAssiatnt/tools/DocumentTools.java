package cs.sonu.personalAssiatnt.tools;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;

import org.springframework.stereotype.Component;

import cs.sonu.personalAssiatnt.model.DocumentInfo;
import cs.sonu.personalAssiatnt.service.DocumentService;

import java.util.List;

@Component
public class DocumentTools {

    private final DocumentService documentService;

    public DocumentTools(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Tool("List the documents available inside a document folder")
    public List<DocumentInfo> listDocuments(
            @P("The document folder name") String folderName) {

        System.out.println(
                "TOOL CALLED -> listDocuments(" + folderName + ")");

        return documentService.listDocuments(folderName);
    }

    @Tool("Find a personal document by its name")
    public DocumentInfo findDocument(
            @P("The name of the document to find") String documentName) {

        System.out.println(
                "TOOL CALLED -> findDocument(" + documentName + ")");

        return documentService.findDocument(documentName);
    }
}