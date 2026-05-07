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
@Schema(description = "Branch representation")
public class BranchResponse {

    @Schema(description = "Branch ID", example = "665f1a2b3c4d5e6f7a8b9c0d")
    private String id;

    @Schema(description = "Branch name", example = "Downtown Branch")
    private String name;

    @Schema(description = "Owning franchise ID", example = "665f1a2b3c4d5e6f7a8b9c0d")
    private String franchiseId;
}
