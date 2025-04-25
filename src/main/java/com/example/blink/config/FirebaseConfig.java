package com.example.blink.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            // Load the service account key file
            var resource = new ClassPathResource("firebase-service-account.json");
            var credentials = GoogleCredentials.fromStream(resource.getInputStream());
            var options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            return FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load firebase-service-account.json. Ensure the file exists in src/main/resources.", e);
        }
    }
}
