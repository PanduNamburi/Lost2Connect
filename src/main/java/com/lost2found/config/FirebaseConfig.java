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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Configuration for Firebase Admin SDK & Firestore database client.
 * Supports loading credentials via Environment Variables (FIREBASE_SERVICE_ACCOUNT_JSON / FIREBASE_CREDENTIALS_BASE64)
 * or local serviceAccountKey.json file.
 */
@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    private final ResourceLoader resourceLoader;

    @Value("${app.firebase.config-path:classpath:serviceAccountKey.json}")
    private String configPath;

    @Value("${app.firebase.project-id:lost2found-94c5a}")
    private String projectId;

    public FirebaseConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public Firestore firestore() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                GoogleCredentials credentials = null;

                // 1. Check for FIREBASE_SERVICE_ACCOUNT_JSON environment variable
                String envJson = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");
                if (envJson != null && !envJson.trim().isEmpty()) {
                    try (InputStream is = new ByteArrayInputStream(envJson.trim().getBytes(StandardCharsets.UTF_8))) {
                        credentials = GoogleCredentials.fromStream(is);
                        log.info("Loaded Firebase service account credentials from FIREBASE_SERVICE_ACCOUNT_JSON environment variable.");
                    }
                }

                // 2. Check for FIREBASE_CREDENTIALS_BASE64 environment variable
                if (credentials == null) {
                    String envBase64 = System.getenv("FIREBASE_CREDENTIALS_BASE64");
                    if (envBase64 != null && !envBase64.trim().isEmpty()) {
                        byte[] decoded = Base64.getDecoder().decode(envBase64.trim());
                        try (InputStream is = new ByteArrayInputStream(decoded)) {
                            credentials = GoogleCredentials.fromStream(is);
                            log.info("Loaded Firebase service account credentials from FIREBASE_CREDENTIALS_BASE64 environment variable.");
                        }
                    }
                }

                // 3. Fallback to local classpath/file resource (e.g. serviceAccountKey.json)
                if (credentials == null) {
                    Resource resource = resourceLoader.getResource(configPath);
                    if (resource.exists()) {
                        try (InputStream serviceAccount = resource.getInputStream()) {
                            credentials = GoogleCredentials.fromStream(serviceAccount);
                            log.info("Loaded Firebase service account credentials from file: {}", configPath);
                        }
                    }
                }

                // 4. Standby default if no credentials present
                if (credentials == null) {
                    log.warn("Firebase credentials not found in env or file. Operating in offline memory mode.");
                    credentials = GoogleCredentials.newBuilder().build();
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .setProjectId(projectId)
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Initialized FirebaseApp with projectId: {}", projectId);
            } catch (Exception e) {
                log.error("Firebase initialization error: ", e);
            }
        }

        try {
            return FirestoreClient.getFirestore();
        } catch (Exception ex) {
            log.error("Firestore client initialization failed: ", ex);
            return null;
        }
    }
}
