package dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.repository;

import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.document.BranchDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchMongoRepository extends ReactiveMongoRepository<BranchDocument, String> {

    Flux<BranchDocument> findByFranchiseId(String franchiseId);

    Mono<Boolean> existsByNameAndFranchiseId(String name, String franchiseId);
}
