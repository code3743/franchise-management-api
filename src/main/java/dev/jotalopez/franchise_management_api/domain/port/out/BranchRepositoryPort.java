package dev.jotalopez.franchise_management_api.domain.port.out;

import dev.jotalopez.franchise_management_api.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepositoryPort {

    Mono<Branch> save(Branch branch);

    Mono<Branch> findById(String id);

    Flux<Branch> findByFranchiseId(String franchiseId);

    Mono<Branch> update(Branch branch);

    Mono<Boolean> existsByNameAndFranchiseId(String name, String franchiseId);
}
