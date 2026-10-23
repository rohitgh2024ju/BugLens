package dev.rohit.buglens.IngestionEngine.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

public class FileHashGenerator {
    FileHashGenerator() {
    }

    public static String generateHash(String clientId, String fileName, long fileSize) {
        Objects.requireNonNull(clientId, "ClientId can not be null");
        Objects.requireNonNull(fileName, "fileId can not be null");

        if (clientId.isBlank()) {
            throw new IllegalArgumentException("clientId can not be null");
        }
        if (fileName.isBlank()) {
            throw new IllegalArgumentException("fileName can not be null");
        }
        if (fileSize < 0) {
            throw new IllegalArgumentException("fileSize can not be negative");
        }

        String input = clientId.length() + ":" + clientId + fileName.length() + ":" + fileName + fileSize;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable", e);
        }
    }
}
