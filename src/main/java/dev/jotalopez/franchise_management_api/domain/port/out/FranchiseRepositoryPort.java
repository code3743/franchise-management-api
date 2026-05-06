package dev.jotalopez.franchise_management_api.domain.port.out;

import dev.jotalopez.franchise_management_api.domain.model.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseRepositoryPort {

    Mono<Franchise> save(Franchise franchise);

    Mono<Franchise> findById(String id);

    Flux<Franchise> findAll();

    Mono<Franchise> update(Franchise franchise);

    Mono<Boolean> existsByName(String name);
}
