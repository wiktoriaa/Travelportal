package app.TravelGo.Document;

import app.TravelGo.Trip.Trip;
import app.TravelGo.Trip.TripService;
import app.TravelGo.User.Auth.AuthService;
import app.TravelGo.User.User;
import app.TravelGo.User.UserService;
import app.TravelGo.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("api/documents")
public class DocumentController {
    final private DocumentService documentService;
    final private TripService tripService;

    final private UserService userService;

    final private AuthService authService;

    @Autowired
    public DocumentController(DocumentService documentService, TripService tripService, UserService userService, AuthService authService) {
        this.documentService = documentService;
        this.tripService = tripService;
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<GetDocumentResponse>> getDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        if (!documents.isEmpty()) {
            List<GetDocumentResponse> documentResponses = new ArrayList<>();

            for (Document document : documents) {
                Optional<User> user = userService.getUserByUsername(document.getUsername());
                if(user.isPresent() && Objects.equals(user.get().getId(), authService.getCurrentUserId())){

                GetDocumentResponse documentResponse = GetDocumentResponse.builder()
                        .id(document.getId())
                        .fileName(document.getFileName())
                        .title(document.getTitle())
                        .tripId(document.getTrip().getId())
                        .username(user.get().getUsername())
                        .build();

                documentResponses.add(documentResponse);
                }
            }

            return ResponseEntity.ok(documentResponses);
        } else {
            return ResponseEntity.noContent().build();
        }
    }


    @GetMapping("/{document_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetDocumentResponse> getDocument(@PathVariable("document_id") Long documentID) {
            Optional<Document> response = documentService.getDocument(documentID);

            if (response.isPresent()) {
                Document document = response.get();
                Optional<User> user = userService.getUserByUsername(document.getUsername());
                if(user.isPresent() && Objects.equals(user.get().getId(), authService.getCurrentUserId())) {
                    GetDocumentResponse documentResponse = GetDocumentResponse.builder()
                            .id(document.getId())
                            .fileName(document.getFileName())
                            .title(document.getTitle())
                            .tripId(document.getTrip().getId())
                            .username(document.getUsername())
                            .build();
                    return ResponseEntity.ok(documentResponse);
                }
                else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GetDocumentResponse());
                }
            } else {
                return ResponseEntity.notFound().build();
            }
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
                    .username(request.getUsername())
                    .build();

            documentService.createDocument(document);

            return ResponseEntity.created(builder.pathSegment("api", "documents", "{id}")
                    .buildAndExpand(document.getId()).toUri()).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{document_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> updateDocument(@PathVariable("document_id") Long documentID, @RequestBody UpdateDocumentRequest request) {
        Optional<Document> existingDocumentOptional = documentService.getDocument(documentID);

        if (existingDocumentOptional.isPresent()) {
            Document existingDocument = existingDocumentOptional.get();
              if (existingDocument.getUsername().equals(authService.getCurrentUser().getUsername())) {
                if (request.getFileName() != null) {
                    existingDocument.setFileName(request.getFileName());
                }
                if (request.getTitle() != null) {
                    existingDocument.setTitle(request.getTitle());
                }
                if (request.getTripId() != null) {
                    existingDocument.setTrip(tripService.getTrip(request.getTripId()).orElse(null));
                }

                documentService.updateDocument(existingDocument);

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
                return ResponseEntity.notFound().build();
        }
    }

}
