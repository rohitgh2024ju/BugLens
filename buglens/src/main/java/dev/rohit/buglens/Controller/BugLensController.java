package dev.rohit.buglens.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.rohit.buglens.Application.InitBugLens;


@RestController
@RequestMapping("/api/buglens")
public class BugLensController {

    private final InitBugLens initBugLens;

    public BugLensController() {
        this.initBugLens = new InitBugLens();
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(
            @RequestParam("file") MultipartFile file,
            @CookieValue("buglens-client-id") String clientId)
            throws Exception {

        Path uploadedFile = saveFile(file);

        initBugLens.run(
                clientId,
                uploadedFile,
                1,
                0.80);

        return ResponseEntity.ok("Analysis complete");
    }
    
    private Path saveFile(MultipartFile file)
        throws IOException {

    Path uploadDirectory =
            Path.of("buglens/uploads");
    Files.createDirectories(uploadDirectory);

    String fileName =
            file.getOriginalFilename();

    if (fileName == null || fileName.isBlank()) {
        throw new IllegalArgumentException(
                "Uploaded file has no filename.");
    }

    Path targetPath =
            uploadDirectory.resolve(fileName);

    Files.copy(
            file.getInputStream(),
            targetPath,
            StandardCopyOption.REPLACE_EXISTING);

    return targetPath;
}
}

