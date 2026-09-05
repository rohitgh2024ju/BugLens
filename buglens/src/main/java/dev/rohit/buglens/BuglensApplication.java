package dev.rohit.buglens;

import dev.rohit.buglens.CollectorEngine.CollectorEngine;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class BuglensApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuglensApplication.class, args);
	}

	@Bean
	public CommandLineRunner runCollector() {
		return args -> {
			// Define your input and output file paths
			Path inputPath = Paths.get("buglens/src/test.log");
			Path outputDirectory = Paths.get("buglens/logs/");

			if (outputDirectory.getParent() != null) {
				Files.createDirectories(outputDirectory.getParent());
			}

			// Execute the collector engine
			CollectorEngine engine = new CollectorEngine(inputPath, outputDirectory, "000");
			engine.collectJson();

			System.out.println("Log ingestion complete: " + outputDirectory.toAbsolutePath());
		};
	}
}