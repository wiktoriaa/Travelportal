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

    public void uploadFeaturePostImage(MultipartFile file, Long postId) throws IOException {
        String uploadDir = this.getPostsImagesDir(postId);
        this.uploadFile(uploadDir, file);
    }

    private String getPostsImagesDir(Long postId) {
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
}
