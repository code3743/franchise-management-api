package dev.jotalopez.franchise_management_api.application.service;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateProductRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateStockRequest;
import dev.jotalopez.franchise_management_api.application.dto.response.ProductResponse;
import dev.jotalopez.franchise_management_api.application.dto.response.TopProductByBranchResponse;
import dev.jotalopez.franchise_management_api.domain.exception.BranchNotFoundException;
import dev.jotalopez.franchise_management_api.domain.exception.ProductNotFoundException;
import dev.jotalopez.franchise_management_api.domain.model.Product;
import dev.jotalopez.franchise_management_api.application.port.in.ProductUseCase;
import dev.jotalopez.franchise_management_api.domain.port.out.BranchRepositoryPort;
import dev.jotalopez.franchise_management_api.domain.port.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort productRepository;
    private final BranchRepositoryPort branchRepository;

    @Override
    public Mono<ProductResponse> addProduct(CreateProductRequest request) {
        return branchRepository.findById(request.getBranchId())
                .switchIfEmpty(Mono.error(new BranchNotFoundException(request.getBranchId())))
                .flatMap(branch -> productRepository.save(
                        Product.builder()
                                .name(request.getName())
                                .stock(request.getStock())
                                .branchId(request.getBranchId())
                                .build()
                ))
                .map(this::toResponse);
    }

    @Override
    public Mono<Void> deleteProduct(String id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)))
                .flatMap(product -> productRepository.deleteById(product.getId()));
    }

    @Override
    public Mono<ProductResponse> updateStock(String id, UpdateStockRequest request) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)))
                .flatMap(product -> {
                    product.setStock(request.getStock());
                    return productRepository.update(product);
                })
                .map(this::toResponse);
    }

    @Override
    public Mono<ProductResponse> updateProductName(String id, UpdateNameRequest request) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)))
                .flatMap(product -> {
                    product.setName(request.getName());
                    return productRepository.update(product);
                })
                .map(this::toResponse);
    }

    @Override
    public Flux<TopProductByBranchResponse> getTopProductPerBranch(String franchiseId) {
        return branchRepository.findByFranchiseId(franchiseId)
                .flatMap(branch ->
                        productRepository.findTopByBranchIdOrderByStockDesc(branch.getId())
                                .map(product -> TopProductByBranchResponse.builder()
                                        .branchId(branch.getId())
                                        .branchName(branch.getName())
                                        .product(toResponse(product))
                                        .build())
                );
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .branchId(product.getBranchId())
                .build();
    }
}
