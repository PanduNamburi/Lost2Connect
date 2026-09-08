package com.lost2found.service;

import com.lost2found.common.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service handling item image file uploads and storage.
 */
@Service
public class ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(ImageStorageService.class);
    private static final String UPLOAD_DIR = "uploads/items/";

    public ImageStorageService() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            log.error("Could not initialize storage directory", e);
        }
    }

    public List<String> storeFiles(List<MultipartFile> files) {
        List<String> storedUrls = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            return storedUrls;
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            String filename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg");
            String extension = "";
            int dotIndex = filename.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = filename.substring(dotIndex).toLowerCase();
            }

            if (!extension.equalsIgnoreCase(".jpg") && !extension.equalsIgnoreCase(".jpeg") &&
                !extension.equalsIgnoreCase(".png") && !extension.equalsIgnoreCase(".webp")) {
                throw new BadRequestException("Only JPEG, PNG, and WebP images are allowed");
            }

            String storedFilename = UUID.randomUUID().toString() + extension;

            try {
                Path destinationFile = Paths.get(UPLOAD_DIR).resolve(storedFilename).normalize().toAbsolutePath();
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                }
                String fileUrl = "/uploads/items/" + storedFilename;
                storedUrls.add(fileUrl);
            } catch (IOException ex) {
                log.error("Failed to store file {}", filename, ex);
                throw new BadRequestException("Failed to store uploaded file: " + filename);
            }
        }

        return storedUrls;
    }
}
