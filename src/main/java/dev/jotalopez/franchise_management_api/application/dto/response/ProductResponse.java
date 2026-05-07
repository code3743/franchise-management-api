package dev.jotalopez.franchise_management_api.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product representation")
public class ProductResponse {

    @Schema(description = "Product ID", example = "665f1a2b3c4d5e6f7a8b9c0d")
    private String id;

    @Schema(description = "Product name", example = "Big Mac")
    private String name;

    @Schema(description = "Available stock", example = "50")
    private Integer stock;

    @Schema(description = "Branch ID where the product belongs", example = "665f1a2b3c4d5e6f7a8b9c0d")
    private String branchId;
}
