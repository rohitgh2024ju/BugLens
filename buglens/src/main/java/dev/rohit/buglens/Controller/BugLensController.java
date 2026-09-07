package dev.rohit.buglens.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.rohit.buglens.Application.InitBugLens;
import dev.rohit.buglens.ClientLifecycleEngine.service.ClientLifecycleService;
import dev.rohit.buglens.Configpaths.BugLensPaths;
import dev.rohit.buglens.IncidentGroupingEngine.model.IncidentGroup;

@RestController
@RequestMapping("/api/buglens")
public class BugLensController {

        private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
        private final InitBugLens initBugLens;
        private final ClientLifecycleService clientLifecycleService;

        public BugLensController(InitBugLens initBugLens, ClientLifecycleService clientLifecycleService) {
                this.initBugLens = initBugLens;
                this.clientLifecycleService = clientLifecycleService;
        }

        @PostMapping("/analyze")
        public ResponseEntity<?> analyze(
                        @RequestParam("file") MultipartFile file,
                        @CookieValue("buglens-client-id") String clientId)
                        throws Exception {

                validateFile(file);

                if (clientLifecycleService.getClientSession(clientId) == null) {
                        clientLifecycleService.registerClient(clientId);
                } else {
                        clientLifecycleService.refreshClient(clientId);
                }

                Path uploadedFile = saveFile(file, clientId);

                List<IncidentGroup> groups = initBugLens.run(
                                clientId,
                                uploadedFile,
                                1,
                                0.80, false);

                return ResponseEntity.ok(groups);
        }

        private Path saveFile(MultipartFile file, String clientId)
                        throws IOException {
                Path uploadDirectory = BugLensPaths.UPLOADS_DIR.resolve(clientId);
                Files.createDirectories(uploadDirectory);

                String originalFilename = file.getOriginalFilename();

                if (originalFilename == null || originalFilename.isBlank()) {
                        throw new IllegalArgumentException(
                                        "Uploaded file has no filename.");
                }

                String fileName = Path.of(originalFilename).getFileName().toString();

                Path targetPath = uploadDirectory.resolve(fileName);

                Files.copy(
                                file.getInputStream(),
                                targetPath,
                                StandardCopyOption.REPLACE_EXISTING);

                return targetPath;
        }

        private void validateFile(MultipartFile file) {
                if (file == null || file.isEmpty()) {
                        throw new IllegalArgumentException("Uploaded file cannot be empty");
                }

                if (file.getSize() > MAX_FILE_SIZE) {
                        throw new IllegalArgumentException(
                                        "File size cannot exceed 10 MB.");
                }

                String originalFilename = file.getOriginalFilename();

                if (originalFilename == null
                                || originalFilename.isBlank()) {

                        throw new IllegalArgumentException(
                                        "Uploaded file has no filename.");
                }
                String fileName = Path.of(originalFilename)
                                .getFileName()
                                .toString()
                                .toLowerCase();

                if (!fileName.endsWith(".log")
                                && !fileName.endsWith(".txt")) {

                        throw new IllegalArgumentException(
                                        "Only .log and .txt files are supported.");
                }

        }
}
