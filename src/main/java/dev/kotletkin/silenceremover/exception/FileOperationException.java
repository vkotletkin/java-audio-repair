package dev.kotletkin.silenceremover.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class FileOperationException extends RuntimeException{

    public FileOperationException(String message) {
        super(message);
    }

    public FileOperationException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public static Supplier<FileOperationException> fileOperationExceptionSupplier(String message, Object... args) {
        return () -> new FileOperationException(message, args);
    }

    public static Supplier<FileOperationException> fileOperationExceptionSupplier(String message) {
        return () -> new FileOperationException(message);
    }
}
