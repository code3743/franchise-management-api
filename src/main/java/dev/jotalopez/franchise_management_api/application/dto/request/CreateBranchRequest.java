package dev.jotalopez.franchise_management_api.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to add a branch to a franchise")
public class CreateBranchRequest {

    @NotBlank(message = "Branch name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Name of the branch", example = "Downtown Branch")
    private String name;

    @NotBlank(message = "Franchise ID is required")
    @Schema(description = "ID of the owning franchise", example = "665f1a2b3c4d5e6f7a8b9c0d")
    private String franchiseId;
}
