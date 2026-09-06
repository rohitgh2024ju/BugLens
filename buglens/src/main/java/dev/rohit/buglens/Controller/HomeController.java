package dev.rohit.buglens.Controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.rohit.buglens.Application.DirectoryInitializer;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/home")
    public ResponseEntity<String> home(
            @CookieValue(value = "buglens-client-id", required = false) String clientId,
            HttpServletResponse response) throws IOException {

        DirectoryInitializer.initialize();

        if (clientId == null) {
            clientId = "client-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

            Cookie cookie = new Cookie("buglens-client-id", clientId);

            cookie.setMaxAge(60 * 60 * 24 * 365 * 10);
            cookie.setPath("/");

            response.addCookie(cookie);
        }

        return ResponseEntity.ok(clientId);
    }
}
