package dev.kotletkin.silenceremover.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class FileValidationException extends RuntimeException {

    public FileValidationException(String message) {
        super(message);
    }

    public FileValidationException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public static Supplier<FileValidationException> fileValidationExceptionSupplier(String message, Object... args) {
        return () -> new FileValidationException(message, args);
    }

    public static Supplier<FileValidationException> fileValidationExceptionSupplier(String message) {
        return () -> new FileValidationException(message);
    }
}