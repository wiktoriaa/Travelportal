package app.TravelGo.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class FileStorageConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.post-images-dir}")
    private String postsImagesDir;

    @Bean
    public void configureFileUploadDirectory() {
        this.dirConfig(uploadDir);
        this.dirConfig(postsImagesDir);
    }

    private void dirConfig(String dir) {
        File directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}