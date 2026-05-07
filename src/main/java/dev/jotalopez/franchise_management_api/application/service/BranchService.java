package dev.jotalopez.franchise_management_api.application.service;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateBranchRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.application.dto.response.BranchResponse;
import dev.jotalopez.franchise_management_api.domain.exception.BranchNotFoundException;
import dev.jotalopez.franchise_management_api.domain.exception.DuplicateNameException;
import dev.jotalopez.franchise_management_api.domain.exception.FranchiseNotFoundException;
import dev.jotalopez.franchise_management_api.domain.model.Branch;
import dev.jotalopez.franchise_management_api.application.port.in.BranchUseCase;
import dev.jotalopez.franchise_management_api.domain.port.out.BranchRepositoryPort;
import dev.jotalopez.franchise_management_api.domain.port.out.FranchiseRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BranchService implements BranchUseCase {

    private final BranchRepositoryPort branchRepository;
    private final FranchiseRepositoryPort franchiseRepository;

    @Override
    public Mono<BranchResponse> addBranch(CreateBranchRequest request) {
        return franchiseRepository.findById(request.getFranchiseId())
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException(request.getFranchiseId())))
                .flatMap(franchise -> branchRepository.existsByNameAndFranchiseId(
                        request.getName(), request.getFranchiseId()))
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new DuplicateNameException(
                                "Branch with name '" + request.getName() + "' already exists in this franchise"));
                    }
                    return branchRepository.save(
                            Branch.builder()
                                    .name(request.getName())
                                    .franchiseId(request.getFranchiseId())
                                    .build()
                    );
                })
                .map(this::toResponse);
    }

    @Override
    public Mono<BranchResponse> updateBranchName(String id, UpdateNameRequest request) {
        return branchRepository.findById(id)
                .switchIfEmpty(Mono.error(new BranchNotFoundException(id)))
                .flatMap(branch -> {
                    branch.setName(request.getName());
                    return branchRepository.update(branch);
                })
                .map(this::toResponse);
    }

    private BranchResponse toResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .franchiseId(branch.getFranchiseId())
                .build();
    }
}
