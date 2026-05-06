package dev.jotalopez.franchise_management_api.domain.port.out;

import dev.jotalopez.franchise_management_api.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepositoryPort {

    Mono<Product> save(Product product);

    Mono<Product> findById(String id);

    Flux<Product> findByBranchId(String branchId);

    Mono<Void> deleteById(String id);

    Mono<Product> update(Product product);

    Mono<Product> findTopByBranchIdOrderByStockDesc(String branchId);
}
