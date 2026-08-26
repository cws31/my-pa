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

    @Tool("List all available personal documents across all folders")
    public List<DocumentInfo> listAllDocuments() {
        System.out.println("TOOL CALLED -> listAllDocuments()");
        return documentService.listAllDocuments();
    }

    @Tool("List all available document folders in the root directory")
    public List<String> listFolders() {
        System.out.println("TOOL CALLED -> listFolders()");
        return documentService.listFolders();
    }

    // @Tool("Create a new document folder")
    // public String createFolder(@P("The name of the new folder") String
    // folderName) {
    // documentService.createFolder(folderName);
    // return "Folder '" + folderName + "' created successfully.";
    // }
}