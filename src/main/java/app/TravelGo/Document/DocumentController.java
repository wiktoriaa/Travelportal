package app.TravelGo.Document;

import app.TravelGo.File.FileService;
import app.TravelGo.Trip.Trip;
import app.TravelGo.Trip.TripService;
import app.TravelGo.User.Auth.AuthService;
import app.TravelGo.User.User;
import app.TravelGo.User.UserService;
import app.TravelGo.dto.GetDocumentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
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
    final private FileService fileService;

    @Autowired
    public DocumentController(DocumentService documentService, TripService tripService, UserService userService,
                              AuthService authService, FileService fileService) {
        this.documentService = documentService;
        this.tripService = tripService;
        this.userService = userService;
        this.authService = authService;
        this.fileService = fileService;
    }

    @GetMapping("trips/{trip_id}/documents")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<GetDocumentResponse>> getDocuments(@PathVariable("trip_id") Long tripId) {
        List<Document> documents = documentService.getAllDocuments();
        if (!documents.isEmpty()) {
            List<GetDocumentResponse> documentResponses = new ArrayList<>();



            for (Document document : documents) {
                if(Objects.equals(document.getTrip().getId(), tripId)){
                    Optional<User> user = userService.getUserByUsername(document.getUsername());

                    if ((user.isPresent() && Objects.equals(user.get().getId(), authService.getCurrentUserId())) ||
                            (authService.getCurrentUser().hasRole("GUIDE")
                                    && document.getTrip().getTripGuides().contains(authService.getCurrentUser()))){

                        GetDocumentResponse documentResponse = GetDocumentResponse.builder()
                                .id(document.getId())
                                .fileName(document.getFileName())
                                .title(document.getTitle())
                                .tripId(document.getTrip().getId())
                                .username(document.getUsername())
                                .pdfPath("/api/files/documents/" + document.getId())
                                .build();

                        documentResponses.add(documentResponse);
                    }
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
                            .pdfPath("/api/files/documents/" + document.getId())
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

        if (trip.isPresent() && (trip.get().getTripGuides().contains(authService.getCurrentUser())) || authService.getCurrentUser().hasRole("MODERATOR"))
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
    public ResponseEntity<Void> createDocument(@PathVariable("trip_id") Long tripId,
                                               @RequestParam("file") MultipartFile file,
                                               @RequestParam("fileName") String fileName,
                                               @RequestParam("title") String title,
                                               @RequestParam("username") String username,
                                               UriComponentsBuilder builder) throws IOException {
        Optional<Trip> trip = tripService.getTrip(tripId);

        if (trip.isPresent()) {
            if (trip.get().getTripGuides().contains(authService.getCurrentUser())) {
                Document document = Document.builder()
                        .fileName(fileName)
                        .title(title)
                        .trip(trip.get())
                        .username(username)
                        .build();

                documentService.createDocument(document);

                if (file != null) {
                    fileService.uploadPDFToDocument(file, document.getId());
                }


                return ResponseEntity.created(builder.pathSegment("api", "documents", "{id}")
                        .buildAndExpand(document.getId()).toUri()).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
