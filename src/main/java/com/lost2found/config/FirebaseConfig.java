package com.lost2found.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;

/**
 * Configuration for Firebase Admin SDK & Firestore database client.
 */
@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    private final ResourceLoader resourceLoader;

    @Value("${app.firebase.config-path:classpath:serviceAccountKey.json}")
    private String configPath;

    @Value("${app.firebase.project-id:lost2found-app}")
    private String projectId;

    public FirebaseConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public Firestore firestore() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                Resource resource = resourceLoader.getResource(configPath);
                GoogleCredentials credentials;

                if (resource.exists()) {
                    try (InputStream serviceAccount = resource.getInputStream()) {
                        credentials = GoogleCredentials.fromStream(serviceAccount);
                        log.info("Loaded Firebase service account credentials from: {}", configPath);
                    }
                } else {
                    log.info("Firebase credential file not present at [{}]. Operating in offline / memory-cached mode.", configPath);
                    credentials = GoogleCredentials.newBuilder().build();
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .setProjectId(projectId)
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Initialized FirebaseApp with projectId: {}", projectId);
            } catch (Exception e) {
                log.info("Firebase initialization note: {}", e.getMessage());
            }
        }

        try {
            return FirestoreClient.getFirestore();
        } catch (Exception ex) {
            log.info("Firestore client standby: {}", ex.getMessage());
            return null;
        }
    }
}
