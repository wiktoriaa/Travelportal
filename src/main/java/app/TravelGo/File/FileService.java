package app.TravelGo.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {
    @Value("${file.post-images-dir}")
    private String postsImagesDir;

    @Value("${file.profile-images-dir}")
    private String profileImagesDir;

    @Value("uploads/documents")
    private String documentsPDFDir;

    public void uploadFeaturePostImage(MultipartFile file, Long postId) throws IOException {
        String uploadDir = this.getPostsImagesDir(postId);
        this.uploadFile(uploadDir, file);
    }

    public String getPostsImagesDir(Long postId) {
        String dir = this.postsImagesDir + '/' + postId.toString();
        File directory = new File(dir);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return dir;
    }

    private void uploadFile(String directory, MultipartFile file) throws IOException {
        StringBuilder fileNames = new StringBuilder();
        Path fileNameAndPath = Paths.get(directory, file.getOriginalFilename());

        fileNames.append(file.getOriginalFilename());
        Files.write(fileNameAndPath, file.getBytes());
    }

    public void uploadProfileImage(MultipartFile file, Long userId) throws IOException {
        String uploadDir = getProfileImagesDir(userId);
        uploadFile(uploadDir, file);
    }

    public String getProfileImagesDir(Long userId) {
        String dir = profileImagesDir + '/' + userId.toString() + '/';
        File directory = new File(dir);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return dir;
    }

    public void uploadPDFToDocument(MultipartFile file, Long documentId) throws IOException {
        String uploadDir = getDocumentsPDFDir(documentId);
        uploadPDFFile(uploadDir, file);
    }

    public String getDocumentsPDFDir(Long documentId) {
        String dir = documentsPDFDir + '/' + documentId.toString();
        File directory = new File(dir);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return dir;
    }

    private void uploadPDFFile(String directory, MultipartFile file) throws IOException {
        Path fileNameAndPath = Paths.get(directory, file.getOriginalFilename());
        Files.write(fileNameAndPath, file.getBytes());
    }
}
