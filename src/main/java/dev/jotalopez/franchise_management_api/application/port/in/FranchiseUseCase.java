package dev.jotalopez.franchise_management_api.application.port.in;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateFranchiseRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.application.dto.response.FranchiseResponse;
import reactor.core.publisher.Mono;

public interface FranchiseUseCase {

    Mono<FranchiseResponse> createFranchise(CreateFranchiseRequest request);

    Mono<FranchiseResponse> updateFranchiseName(String id, UpdateNameRequest request);
}
