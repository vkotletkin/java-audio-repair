package dev.kotletkin.silenceremover.service.impl;

import dev.kotletkin.silenceremover.exception.FileOperationException;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private static final Path TMP_DIRECTORY = Path.of("./tmp");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("wav", "pcm");

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
        } catch (IOException _) {
            throw new FileOperationException("Problems saving the file");
        }

    }

    public void deleteFile(List<String> paths) {
        try {
            for (String path : paths) Files.delete(Path.of(path));
        } catch (IOException _) {
            throw new FileOperationException("Problems deleting the file");
        }
    }

    private void initUploadDirectory() {

        try {
            if (!Files.exists(TMP_DIRECTORY)) {
                Files.createDirectories(TMP_DIRECTORY);
                log.info("Created upload directory: {}", TMP_DIRECTORY);
            }

            if (!Files.isWritable(TMP_DIRECTORY)) {
                throw new FileOperationException("The download directory is not writable");
            }
            log.info("The file upload directory has been created: {}", TMP_DIRECTORY);
        } catch (IOException _) {
            throw new FileOperationException("Error initializing the directory: {0}", TMP_DIRECTORY);
        }
    }

    private void validateFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new FileValidationException("The file is empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw new FileValidationException("Invalid filename");
        }

        if (!ALLOWED_EXTENSIONS.isEmpty()) {
            String extension = getFileExtension(originalFilename).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new FileValidationException("File type not allowed. Allowed: " + ALLOWED_EXTENSIONS);
            }
        }
    }

    private String generateSecureFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String baseName = UUID.randomUUID().toString();
        return baseName + "." + extension;
    }

    private long saveFileToDisk(MultipartFile file, Path targetPath) throws IOException {

        Path tempPath = targetPath.getParent().resolve(targetPath.getFileName() + ".tmp");


        try (InputStream inputStream = file.getInputStream()) {

            Files.copy(inputStream, tempPath, StandardCopyOption.REPLACE_EXISTING);
            Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE);

            return Files.size(targetPath);

        } finally {
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
