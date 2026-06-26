package com.ttip.mesa_agil.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${upload.path}")
    private String uploadPath;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    public String save(MultipartFile file) throws IOException {
        // Pasar errores a controller error handler
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar una imagen.");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Formato de imagen no permitido.");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("La imagen supera el tamaño máximo de 5MB.");
        }

        String extension = Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf("."));

        String fileName = UUID.randomUUID() + extension;

        Path uploadDir = Paths.get(uploadPath);

        Files.createDirectories(uploadDir);

        Files.copy(
                file.getInputStream(),
                uploadDir.resolve(fileName),
                StandardCopyOption.REPLACE_EXISTING
        );

        return "/uploads/" + fileName;
    }
}
