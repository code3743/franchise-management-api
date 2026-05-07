package dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.repository;

import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.document.FranchiseDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface FranchiseMongoRepository extends ReactiveMongoRepository<FranchiseDocument, String> {

    Mono<Boolean> existsByName(String name);

    Mono<FranchiseDocument> findByName(String name);
}
