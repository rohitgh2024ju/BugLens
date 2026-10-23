
package dev.rohit.buglens.IngestionEngine.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONObject;

import dev.rohit.buglens.Configpaths.BugLensPaths;

import static dev.rohit.buglens.IngestionEngine.util.FileHashGenerator.generateHash;

public class HashConfigManager {

    private static final Path filePath = BugLensPaths.FILE_HASHES_CONFIG;

    public HashConfigManager() {
    }

    public void initiateFile() throws IOException {
        if (Files.notExists(filePath)) {
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
        }
    }

    private static JSONObject readAllHashJson() throws IOException {
        String content = Files.readString(filePath);

        if (content.isBlank()) {
            return new JSONObject();
        }

        return new JSONObject(content);
    }

    public void registerHash(String hash, String clientId)
            throws IOException {

        JSONObject jsonObject = readAllHashJson();

        String existingHash = readClientHash(jsonObject, clientId);

        if (existingHash != null) {
            JSONObject entry = jsonObject.getJSONObject(existingHash);

            entry.put("hashId", hash);

            jsonObject.remove(existingHash);
            jsonObject.put(hash, entry);
        } else {
            JSONObject entry = new JSONObject();
            entry.put("clientId", clientId);
            entry.put("hashId", hash);

            jsonObject.put(hash, entry);
        }

        Files.writeString(filePath, jsonObject.toString(4));
    }

    private String readClientHash(
            JSONObject jsonObject, String clientId) {

        for (String key : jsonObject.keySet()) {
            JSONObject entry = jsonObject.getJSONObject(key);

            if (entry.getString("clientId").equals(clientId)) {
                return key;
            }
        }

        return null;
    }

    public boolean isHashEquals(String hash, String clientId)
            throws IOException {

        JSONObject jsonObject = readAllHashJson();
        String existingHash = readClientHash(jsonObject, clientId);

        if (hash.equals(existingHash)) {
            return true;
        }

        registerHash(hash, clientId);
        return false;
    }

    public static void main(String[] args) throws IOException {
        String hash = generateHash("clt101A", "server.log", 101);

        HashConfigManager manager = new HashConfigManager();

        boolean sameHash = manager.isHashEquals(hash, "clt101A");

        System.out.println("Hash: " + hash);
        System.out.println("Hash matches: " + sameHash);
    }
}
