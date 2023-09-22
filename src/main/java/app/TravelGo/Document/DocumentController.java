package app.TravelGo.Document;

import app.TravelGo.Trip.Trip;
import app.TravelGo.Trip.TripService;
import app.TravelGo.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/documents")
public class DocumentController {
    final private DocumentService documentService;
    final private TripService tripService;

    @Autowired
    public DocumentController(DocumentService documentService, TripService tripService) {
        this.documentService = documentService;
        this.tripService = tripService;
    }

    @GetMapping("/{document_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetDocumentResponse> getDocument(@PathVariable("document_id") Long docuemntID) {
        Optional<Document> response = documentService.getDocument(docuemntID);
        if (response.isPresent()) {
            Document document = response.get();
            GetDocumentResponse documentResponse = GetDocumentResponse.builder()
                    .id(document.getId())
                    .file_name(document.getFileName())
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


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createDocument(@RequestBody CreateDocumentRequest request, UriComponentsBuilder builder) {

            Optional<Trip> trip = tripService.getTrip(request.getTripId());
            if (trip.isPresent()) {
                Document document = Document.builder()
                        .fileName(request.getFileName())
                        .title(request.getTitle())
                        .trip(trip.get())
                        .build();
                documentService.createDocument(document);
                return ResponseEntity.created(builder.pathSegment("api", "documents", "{id}")
                        .buildAndExpand(document.getId()).toUri()).build();
            } else {
                return ResponseEntity.notFound().build();
            }
    }

}
