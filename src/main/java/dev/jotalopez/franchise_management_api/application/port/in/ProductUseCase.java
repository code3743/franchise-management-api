package dev.jotalopez.franchise_management_api.application.port.in;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateProductRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateStockRequest;
import dev.jotalopez.franchise_management_api.application.dto.response.ProductResponse;
import dev.jotalopez.franchise_management_api.application.dto.response.TopProductByBranchResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductUseCase {

    Mono<ProductResponse> addProduct(CreateProductRequest request);

    Mono<Void> deleteProduct(String id);

    Mono<ProductResponse> updateStock(String id, UpdateStockRequest request);

    Mono<ProductResponse> updateProductName(String id, UpdateNameRequest request);

    Flux<TopProductByBranchResponse> getTopProductPerBranch(String franchiseId);
}
