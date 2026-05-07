package dev.jotalopez.franchise_management_api.application.service;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateFranchiseRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.application.dto.response.FranchiseResponse;
import dev.jotalopez.franchise_management_api.domain.exception.DuplicateNameException;
import dev.jotalopez.franchise_management_api.domain.exception.FranchiseNotFoundException;
import dev.jotalopez.franchise_management_api.domain.model.Franchise;
import dev.jotalopez.franchise_management_api.application.port.in.FranchiseUseCase;
import dev.jotalopez.franchise_management_api.domain.port.out.FranchiseRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FranchiseService implements FranchiseUseCase {

    private final FranchiseRepositoryPort franchiseRepository;

    @Override
    public Mono<FranchiseResponse> createFranchise(CreateFranchiseRequest request) {
        return franchiseRepository.existsByName(request.getName())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new DuplicateNameException(
                                "Franchise with name '" + request.getName() + "' already exists"));
                    }
                    return franchiseRepository.save(
                            Franchise.builder()
                                    .name(request.getName())
                                    .build()
                    );
                })
                .map(this::toResponse);
    }

    @Override
    public Mono<FranchiseResponse> updateFranchiseName(String id, UpdateNameRequest request) {
        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(id)))
                .flatMap(franchise -> {
                    franchise.setName(request.getName());
                    return franchiseRepository.update(franchise);
                })
                .map(this::toResponse);
    }

    private FranchiseResponse toResponse(Franchise franchise) {
        return FranchiseResponse.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .build();
    }
}
