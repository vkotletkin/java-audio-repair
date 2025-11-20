package dev.kotletkin.silenceremover.service.impl;

import dev.kotletkin.silenceremover.exception.FileValidationException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private static final Path TMP_DIRECTORY = Path.of("./tmp");
    private static final int WAV_MAX_SIZE_BYTES = 419430400;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("wav");

    @PostConstruct
    public void init() {
        initUploadDirectory();
    }

    public FileSaveResult saveFile(MultipartFile multipartFile) {

        checkFileIsEmpty(multipartFile);
        validateFile(multipartFile);
        String safeFilename = generateSecureFilename(multipartFile.getOriginalFilename());
        Path targetPath = TMP_DIRECTORY.resolve(safeFilename);

        try {
            long fileSize = saveFileToDisk(multipartFile, targetPath);
            return new FileSaveResult(safeFilename, targetPath.toString(), fileSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public byte[] readFileToByteArray(String filePath) {

        try {
            Path path = Path.of(filePath);

            if (!Files.exists(path)) {
                throw new FileValidationException("File not found: " + filePath);
            }

            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("problema s read to array");
        }
    }

    private void initUploadDirectory() {

        try {
            if (!Files.exists(TMP_DIRECTORY)) {
                Files.createDirectories(TMP_DIRECTORY);
                log.info("Created upload directory: {}", TMP_DIRECTORY);
            }

            // Проверяем права на запись
            if (!Files.isWritable(TMP_DIRECTORY)) {
                throw new IOException("Upload directory is not writable: " + TMP_DIRECTORY);
            }

            log.info("Upload directory is ready: {}", TMP_DIRECTORY);

        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize upload directory: " + TMP_DIRECTORY, e);
        }
    }

    private void validateFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new FileValidationException("File is empty");
        }

        if (file.getSize() > WAV_MAX_SIZE_BYTES) {
            throw new FileValidationException("File size exceeds limit: " + WAV_MAX_SIZE_BYTES + " bytes");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw new FileValidationException("Invalid filename");
        }

        // Проверка расширения файла
        if (!ALLOWED_EXTENSIONS.isEmpty()) {
            String extension = getFileExtension(originalFilename).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new FileValidationException("File type not allowed. Allowed: " + ALLOWED_EXTENSIONS);
            }
        }

        // Дополнительная проверка для WAV файлов
        if (!isValidWavFile(file)) {
            throw new FileValidationException("Invalid WAV file");
        }
    }

    private boolean isValidWavFile(MultipartFile file) {
        try {
            // Базовая проверка сигнатуры WAV файла
            byte[] header = new byte[4];
            try (InputStream is = file.getInputStream()) {
                if (is.read(header) != 4) return false;
            }
            // Проверка на "RIFF" сигнатуру
            return header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F';
        } catch (IOException _) {
            return false;
        }
    }

    private String generateSecureFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String baseName = UUID.randomUUID().toString();
        return extension != null ? baseName + "." + extension : baseName;
    }

    private long saveFileToDisk(MultipartFile file, Path targetPath) throws IOException {
        // Используем временный файл для атомарности
        Path tempPath = targetPath.getParent().resolve(targetPath.getFileName() + ".tmp");

        try {
            // Копируем во временный файл
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, tempPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Переименовываем в целевой файл (атомарная операция)
            Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE);

            return Files.size(targetPath);

        } finally {
            // Удаляем временный файл, если он остался
            if (Files.exists(tempPath)) {
                Files.deleteIfExists(tempPath);
            }
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private void checkFileIsEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileValidationException("File is empty. Filename: {0}", file.getOriginalFilename());
        }
    }

    public record FileSaveResult(String filename, String filePath, long size) {
    }
}
