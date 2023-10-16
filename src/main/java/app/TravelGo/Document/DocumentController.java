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
@RequestMapping("api/")
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

    @GetMapping("trips/{trip_id}/documents")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<GetDocumentResponse>> getDocuments() {
        List<Document> documents = documentService.getAllDocuments();
        if (!documents.isEmpty()) {
            List<GetDocumentResponse> documentResponses = new ArrayList<>();

            for (Document document : documents) {
                Optional<User> user = userService.getUserByUsername(document.getUsername());
                if ((user.isPresent() && Objects.equals(user.get().getId(), authService.getCurrentUserId())) ||
                        (authService.getCurrentUser().hasRole("GUIDE") &&
                                Objects.equals(document.getTrip().getTripGuide().getId(), authService.getCurrentUserId()))) {

                    GetDocumentResponse documentResponse = GetDocumentResponse.builder()
                        .id(document.getId())
                        .fileName(document.getFileName())
                        .title(document.getTitle())
                        .tripId(document.getTrip().getId())
                        .username(document.getUsername())
                        .build();

                documentResponses.add(documentResponse);
                }
            }

            return ResponseEntity.ok(documentResponses);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("trips/{trip_id}/documents/{document_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetDocumentResponse> getDocument(@PathVariable("trip_id") Long tripId, @PathVariable("document_id") Long documentID) {
        Optional<Trip> tripOptional = tripService.getTrip(tripId);

        if (tripOptional.isPresent()) {
            Trip trip = tripOptional.get();
            Optional<Document> documentOptional = documentService.getDocument(documentID);

            if (documentOptional.isPresent() && documentOptional.get().getTrip().equals(trip)) {
                Document document = documentOptional.get();
                Optional<User> user = userService.getUserByUsername(document.getUsername());

                if (user.isPresent() && Objects.equals(user.get().getId(), authService.getCurrentUserId())) {
                    GetDocumentResponse documentResponse = GetDocumentResponse.builder()
                            .id(document.getId())
                            .fileName(document.getFileName())
                            .title(document.getTitle())
                            .tripId(document.getTrip().getId())
                            .username(document.getUsername())
                            .build();
                    return ResponseEntity.ok(documentResponse);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GetDocumentResponse());
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("trips/{trip_id}/documents/{document_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteDocument(@PathVariable("trip_id") Long tripId, @PathVariable("document_id") Long documentID) {
        Optional<Trip> trip = tripService.getTrip(tripId);

        if (trip.isPresent() && (Objects.equals(trip.get().getTripGuide().getId(), authService.getCurrentUserId()) || authService.getCurrentUser().hasRole("MODERATOR")))
        {

            boolean success = documentService.deleteDocument(documentID);
            if (success) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } return ResponseEntity.notFound().build();

    }


    @PostMapping("trips/{trip_id}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createDocument(@PathVariable("trip_id") Long tripId, @RequestBody CreateDocumentRequest request, UriComponentsBuilder builder) {
        Optional<Trip> trip = tripService.getTrip(tripId);

        if (trip.isPresent() && Objects.equals(trip.get().getTripGuide().getId(), authService.getCurrentUserId())) {
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

}
