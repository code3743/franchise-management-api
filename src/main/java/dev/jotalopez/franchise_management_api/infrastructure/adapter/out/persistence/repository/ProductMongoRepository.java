package dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.repository;

import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.document.ProductDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductMongoRepository extends ReactiveMongoRepository<ProductDocument, String> {

    Flux<ProductDocument> findByBranchId(String branchId);

    Mono<ProductDocument> findTopByBranchIdOrderByStockDesc(String branchId);
}
