package dev.jotalopez.franchise_management_api.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to add a product to a branch")
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Name of the product", example = "Big Mac")
    private String name;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Schema(description = "Initial stock quantity", example = "50")
    private Integer stock;

    @NotBlank(message = "Branch ID is required")
    @Schema(description = "ID of the branch where the product belongs", example = "665f1a2b3c4d5e6f7a8b9c0d")
    private String branchId;
}
