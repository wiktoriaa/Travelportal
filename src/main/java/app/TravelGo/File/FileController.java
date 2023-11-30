package app.TravelGo.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("api/files")
public class FileController {

    @Value("${file.post-images-dir}")
    private String postsImagesDir;
    @Value("${file.profile-images-dir}")
    private String profileImagesDir;

    @Value("${file.documents-pdf-dir}")
    private String documentsPDFDir;

    @GetMapping("/posts/{postDir}")
    @ResponseBody
    public ResponseEntity<List<String>> listAllFiles(@PathVariable String postDir) {
        File dir = new File(postsImagesDir + '/' + postDir);
        File[] files = dir.listFiles();
        List<String> fileNames = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    Resource fileResource = new FileSystemResource(file);
                    fileNames.add(fileResource.getFilename());
                }
            }
        }

        return ResponseEntity.ok(fileNames);
    }


    @GetMapping("/posts/{postDir}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<byte[]> serveFeaturePost(@PathVariable String filename, @PathVariable String postDir) {
        try {
            Path imagePath = Paths.get(this.postsImagesDir, postDir, filename);
            byte[] imageBytes = Files.readAllBytes(imagePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/profile/{userId}")
    @ResponseBody
    public ResponseEntity<byte[]> serveProfileImage(@PathVariable String userId) {
        try {
            File directory = new File(profileImagesDir + '/' + userId);

            if (!directory.exists()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            File[] files = directory.listFiles();

            if (files != null && files.length > 0) {
                File firstFile = files[0];
                String filename = firstFile.getName();

                Path imagePath = Paths.get(profileImagesDir, userId, filename);

                byte[] imageBytes = Files.readAllBytes(imagePath);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);

                return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/documents/{documentId}")
    @ResponseBody
    public ResponseEntity<byte[]> serveDocument(@PathVariable Long documentId) {
        try {
            String documentDir = Paths.get(this.documentsPDFDir, documentId.toString()).toString();
            File directory = new File(documentDir);

            if (!directory.exists()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            File[] files = directory.listFiles();

            if (files != null && files.length > 0) {
                File firstFile = files[0];
                String filename = firstFile.getName();

                Path documentPath = Paths.get(documentDir, filename);
                byte[] documentBytes = Files.readAllBytes(documentPath);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);

                return new ResponseEntity<>(documentBytes, headers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
