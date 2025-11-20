package dev.kotletkin.silenceremover.exception.dto;

import java.util.List;

public record ValidationErrorResponse(List<ErrorResponse> validationErrors) {
}
