package app.TravelGo.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    final private DocumentRepository documentRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Optional<Document> getDocument(Long documentID) {
        return documentRepository.findById(documentID);
    }

    public void createDocument(Document document) {
        documentRepository.save(document);
    }

    public boolean deleteDocument(Long documentID) {
        if (documentRepository.existsById(documentID)) {
            documentRepository.deleteById(documentID);
            return true;
        }
        return false;
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public void updateDocument(Document document) {
        documentRepository.save(document);
    }
}