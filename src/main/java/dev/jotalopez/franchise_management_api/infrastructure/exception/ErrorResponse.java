package dev.jotalopez.franchise_management_api.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response")
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Timestamp of the error", example = "2024-01-01T00:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "HTTP error description", example = "Not Found")
    private String error;

    @Schema(description = "Detailed error message", example = "Franchise not found with id: abc123")
    private String message;

    @Schema(description = "Request path that triggered the error", example = "/api/franchises/abc123")
    private String path;
}
