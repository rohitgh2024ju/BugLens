package dev.rohit.buglens.IngestionEngine.util;

import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class HashService {

    public Boolean initiateService(
            String clientId, String fileName, long fileSize)
            throws IOException {

        HashConfigManager manager = new HashConfigManager();

        manager.initiateFile();

        String hash = FileHashGenerator.generateHash(
                clientId, fileName, fileSize);

        boolean sameHash = manager.isHashEquals(hash, clientId);

        if (sameHash) {
            return true;
        }

        return false;
    }
}
