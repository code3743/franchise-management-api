package dev.jotalopez.franchise_management_api.infrastructure.adapter.in.web;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateFranchiseRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.application.dto.response.FranchiseResponse;
import dev.jotalopez.franchise_management_api.application.port.in.FranchiseUseCase;
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
@RequestMapping("/api/franchises")
@RequiredArgsConstructor
@Tag(name = "Franchises", description = "Operations for managing franchises")
public class FranchiseController {

    private final FranchiseUseCase franchiseUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new franchise")
    @ApiResponse(responseCode = "201", description = "Franchise created")
    @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Franchise name already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<FranchiseResponse> create(@Valid @RequestBody CreateFranchiseRequest request) {
        return franchiseUseCase.createFranchise(request);
    }

    @PatchMapping("/{id}/name")
    @Operation(summary = "Update franchise name")
    @ApiResponse(responseCode = "200", description = "Name updated")
    @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Franchise not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<FranchiseResponse> updateName(
            @PathVariable String id,
            @Valid @RequestBody UpdateNameRequest request) {
        return franchiseUseCase.updateFranchiseName(id, request);
    }
}
