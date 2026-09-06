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
import dev.rohit.buglens.IncidentGroupingEngine.model.IncidentGroup;

@RestController
@RequestMapping("/api/buglens")
public class BugLensController {

        private final InitBugLens initBugLens;
        private final ClientLifecycleService clientLifecycleService;

        public BugLensController(ClientLifecycleService clientLifecycleService) {
                this.clientLifecycleService = clientLifecycleService;
                this.initBugLens = new InitBugLens();
        }

        @PostMapping("/analyze")
        public ResponseEntity<?> analyze(
                        @RequestParam("file") MultipartFile file,
                        @CookieValue("buglens-client-id") String clientId)
                        throws Exception {

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

                Path uploadDirectory = Path.of("buglens/uploads", clientId);
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
}
