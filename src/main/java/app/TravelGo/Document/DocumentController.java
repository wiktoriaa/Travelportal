package app.TravelGo.Document;

import app.TravelGo.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/documents")
public class DocumentController {
    final private DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/{document_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetDocumentResponse> getDocument(@PathVariable("document_id") Long docuemntID) {
        Optional<Document> response = documentService.getDocument(docuemntID);
        if (response.isPresent()) {
            Document document = response.get();
            GetDocumentResponse documentResponse = GetDocumentResponse.builder()
                    .id(document.getId())
                    .file_name(document.getFile_name())
                    .title(document.getTitle())
                    .trip_id(document.getTrip().getId())
                    .build();
            return ResponseEntity.ok(documentResponse);
        }
        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{document_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteDocument(@PathVariable("document_id") Long documentID) {
        boolean success = documentService.deleteDocument(documentID);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    //TODO createDocument()



}
