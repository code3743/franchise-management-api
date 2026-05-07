package dev.jotalopez.franchise_management_api.infrastructure.adapter.in.web;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateBranchRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.application.dto.response.BranchResponse;
import dev.jotalopez.franchise_management_api.application.port.in.BranchUseCase;
import dev.jotalopez.franchise_management_api.infrastructure.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
@Tag(name = "Branches", description = "Operations for managing branches")
public class BranchController {

    private final BranchUseCase branchUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a branch to a franchise")
    @ApiResponse(responseCode = "201", description = "Branch created")
    @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Franchise not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Branch name already exists in this franchise",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<BranchResponse> create(@Valid @RequestBody CreateBranchRequest request) {
        return branchUseCase.addBranch(request);
    }

    @PatchMapping("/{id}/name")
    @Operation(summary = "Update branch name")
    @ApiResponse(responseCode = "200", description = "Name updated")
    @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Branch not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<BranchResponse> updateName(
            @PathVariable String id,
            @Valid @RequestBody UpdateNameRequest request) {
        return branchUseCase.updateBranchName(id, request);
    }
}
