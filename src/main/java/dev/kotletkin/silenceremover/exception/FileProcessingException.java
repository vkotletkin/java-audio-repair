package dev.kotletkin.silenceremover.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class FileProcessingException extends RuntimeException{

    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public static Supplier<FileProcessingException> fileProcessingExceptionSupplier(String message, Object... args) {
        return () -> new FileProcessingException(message, args);
    }

    public static Supplier<FileProcessingException> fileProcessingExceptionSupplier(String message) {
        return () -> new FileProcessingException(message);
    }
}
