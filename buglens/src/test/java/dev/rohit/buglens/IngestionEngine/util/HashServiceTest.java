package dev.rohit.buglens.IngestionEngine.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class HashServiceTest {

    private final HashService hashService = new HashService();

    @Test
    void shouldGenerateSameHashForSameMetadata() {
        String first = hashService.generateFileHash(
                "client-001", "server.log", 1024);

        String second = hashService.generateFileHash(
                "client-001", "server.log", 1024);

        assertEquals(first, second);
    }

    @Test
    void shouldGenerateDifferentHashForDifferentClient() {
        String first = hashService.generateFileHash(
                "client-001", "server.log", 1024);

        String second = hashService.generateFileHash(
                "client-002", "server.log", 1024);

        assertNotEquals(first, second);
    }

    @Test
    void shouldGenerateDifferentHashForDifferentFileSize() {
        String first = hashService.generateFileHash(
                "client-001", "server.log", 1024);

        String second = hashService.generateFileHash(
                "client-001", "server.log", 2048);

        assertNotEquals(first, second);
    }

    @Test
    void shouldRejectBlankClientId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> hashService.generateFileHash(
                        " ", "server.log", 1024));
    }
}