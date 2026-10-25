package dev.rohit.buglens.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.rohit.buglens.Application.AnalysisCacheService;
import dev.rohit.buglens.Application.AnalysisResponse;
import dev.rohit.buglens.Application.InitBugLens;
import dev.rohit.buglens.ClientLifecycleEngine.service.ClientLifecycleService;
import dev.rohit.buglens.Configpaths.BugLensPaths;
import dev.rohit.buglens.IngestionEngine.util.HashService;

@RestController
@RequestMapping("/api/buglens")
public class BugLensController {

        private final HashService hashService;
        private final AnalysisCacheService analysisCacheService;

        private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
        private final InitBugLens initBugLens;
        private final ClientLifecycleService clientLifecycleService;

        public BugLensController(InitBugLens initBugLens, ClientLifecycleService clientLifecycleService,
                        HashService hashService, AnalysisCacheService analysisCacheService) {
                this.initBugLens = initBugLens;
                this.clientLifecycleService = clientLifecycleService;
                this.hashService = hashService;
                this.analysisCacheService = analysisCacheService;
        }

        @PostMapping("/analyze")
        public ResponseEntity<?> analyze(
                        @RequestParam("file") MultipartFile file,
                        @CookieValue("buglens-client-id") String clientId,
                        @RequestHeader(name = "X-BugLens-Cache", defaultValue = "true") boolean useCache)
                        throws Exception {

                validateFile(file);

                if (clientLifecycleService.getClientSession(clientId) == null) {
                        clientLifecycleService.registerClient(clientId);
                } else {
                        clientLifecycleService.refreshClient(clientId);
                }

                Path uploadedFile = saveFile(file, clientId);

                String fileName = uploadedFile.getFileName().toString();
                long fileSize = Files.size(uploadedFile);

                boolean sameHash = hashService.initiateService(clientId, fileName, fileSize);

                if (useCache && sameHash) {

                        AnalysisResponse cachedResponse = analysisCacheService.getCacheResponse(
                                        clientId, 10, 0.80, false);

                        if (cachedResponse != null) {
                                return ResponseEntity.ok()
                                                .header("X-BugLens-Cache", "HIT")
                                                .body(cachedResponse);
                        }
                }

                if (!sameHash) {
                        analysisCacheService.invalidate(clientId);
                }

                AnalysisResponse response = initBugLens.run(
                                clientId,
                                uploadedFile,
                                10,
                                0.80,
                                false);

                analysisCacheService.cacheResponse(
                                clientId,
                                10,
                                0.80,
                                false,
                                response);

                String cacheStatus = useCache ? "MISS" : "BYPASS";

                return ResponseEntity.ok()
                                .header("X-BugLens-Cache", cacheStatus)
                                .body(response);
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
