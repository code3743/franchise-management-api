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
@Schema(description = "Franchise representation")
public class FranchiseResponse {

    @Schema(description = "Franchise ID", example = "665f1a2b3c4d5e6f7a8b9c0d")
    private String id;

    @Schema(description = "Franchise name", example = "McDonald's")
    private String name;
}
