package dev.jotalopez.franchise_management_api.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update product stock")
public class UpdateStockRequest {

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Schema(description = "New stock quantity", example = "100")
    private Integer stock;
}
