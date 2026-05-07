package dev.jotalopez.franchise_management_api.application.port.in;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateBranchRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.application.dto.response.BranchResponse;
import reactor.core.publisher.Mono;

public interface BranchUseCase {

    Mono<BranchResponse> addBranch(CreateBranchRequest request);

    Mono<BranchResponse> updateBranchName(String id, UpdateNameRequest request);
}
