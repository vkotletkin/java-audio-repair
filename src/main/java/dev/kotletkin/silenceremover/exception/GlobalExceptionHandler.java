package dev.kotletkin.silenceremover.exception;

import dev.kotletkin.silenceremover.exception.dto.ErrorResponse;
import dev.kotletkin.silenceremover.exception.dto.ValidationErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler
    public ErrorResponse handleFileValidationException(FileValidationException e) {
        log.error(e.getMessage());
        return new ErrorResponse("File validation error", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleFileProcessingException(FileProcessingException e) {
        log.error(e.getMessage());
        return new ErrorResponse("File processing error", e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler
    public ValidationErrorResponse handleOnConstraintValidationException(ConstraintViolationException e) {

        final List<ErrorResponse> errorResponses = e.getConstraintViolations().stream()
                .map(error -> new ErrorResponse(
                                error.getPropertyPath().toString(),
                                error.getMessage()
                        )
                )
                .toList();

        log.error(e.getMessage());

        return new ValidationErrorResponse(errorResponses);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler
    public ValidationErrorResponse handleOnMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        final List<ErrorResponse> errorResponses = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponse(error.getField(), error.getDefaultMessage()))
                .toList();

        log.error(e.getMessage());

        return new ValidationErrorResponse(errorResponses);
    }
}