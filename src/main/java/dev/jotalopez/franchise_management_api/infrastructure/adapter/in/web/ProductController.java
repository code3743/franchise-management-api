package dev.jotalopez.franchise_management_api.infrastructure.adapter.in.web;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateProductRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateStockRequest;
import dev.jotalopez.franchise_management_api.application.dto.response.ProductResponse;
import dev.jotalopez.franchise_management_api.application.dto.response.TopProductByBranchResponse;
import dev.jotalopez.franchise_management_api.application.port.in.ProductUseCase;
import dev.jotalopez.franchise_management_api.infrastructure.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Operations for managing products")
public class ProductController {

    private final ProductUseCase productUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a product to a branch")
    @ApiResponse(responseCode = "201", description = "Product created")
    @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Branch not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        return productUseCase.addProduct(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a product")
    @ApiResponse(responseCode = "204", description = "Product deleted")
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<Void> delete(@PathVariable String id) {
        return productUseCase.deleteProduct(id);
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock")
    @ApiResponse(responseCode = "200", description = "Stock updated")
    @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ProductResponse> updateStock(
            @PathVariable String id,
            @Valid @RequestBody UpdateStockRequest request) {
        return productUseCase.updateStock(id, request);
    }

    @PatchMapping("/{id}/name")
    @Operation(summary = "Update product name")
    @ApiResponse(responseCode = "200", description = "Name updated")
    @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Mono<ProductResponse> updateName(
            @PathVariable String id,
            @Valid @RequestBody UpdateNameRequest request) {
        return productUseCase.updateProductName(id, request);
    }

    @GetMapping("/top-stock/franchise/{franchiseId}")
    @Operation(summary = "Get product with highest stock per branch for a franchise")
        @ApiResponse(responseCode = "200", description = "Top products retrieved")
        @ApiResponse(responseCode = "404", description = "Franchise not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public Flux<TopProductByBranchResponse> getTopByFranchise(@PathVariable String franchiseId) {
        return productUseCase.getTopProductPerBranch(franchiseId);
    }
}
