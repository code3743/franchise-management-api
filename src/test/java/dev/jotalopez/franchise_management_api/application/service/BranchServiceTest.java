package dev.jotalopez.franchise_management_api.application.service;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateBranchRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.domain.exception.BranchNotFoundException;
import dev.jotalopez.franchise_management_api.domain.exception.DuplicateNameException;
import dev.jotalopez.franchise_management_api.domain.exception.FranchiseNotFoundException;
import dev.jotalopez.franchise_management_api.domain.model.Branch;
import dev.jotalopez.franchise_management_api.domain.model.Franchise;
import dev.jotalopez.franchise_management_api.domain.port.out.BranchRepositoryPort;
import dev.jotalopez.franchise_management_api.domain.port.out.FranchiseRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {

    @Mock
    private BranchRepositoryPort branchRepository;

    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @InjectMocks
    private BranchService branchService;

    @Test
    @DisplayName("addBranch - valid request - returns saved branch")
    void addBranch_whenValid_returnsSavedBranch() {
        CreateBranchRequest request = CreateBranchRequest.builder()
                .name("Downtown")
                .franchiseId("f1")
                .build();
        Franchise franchise = Franchise.builder().id("f1").name("McDonald's").build();
        Branch saved = Branch.builder().id("b1").name("Downtown").franchiseId("f1").build();

        when(franchiseRepository.findById("f1")).thenReturn(Mono.just(franchise));
        when(branchRepository.existsByNameAndFranchiseId("Downtown", "f1")).thenReturn(Mono.just(false));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(branchService.addBranch(request))
                .assertNext(response -> {
                    assertThat(response.getId()).isEqualTo("b1");
                    assertThat(response.getName()).isEqualTo("Downtown");
                    assertThat(response.getFranchiseId()).isEqualTo("f1");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("addBranch - franchise not found - throws FranchiseNotFoundException")
    void addBranch_whenFranchiseNotFound_throwsFranchiseNotFoundException() {
        CreateBranchRequest request = CreateBranchRequest.builder()
                .name("Downtown")
                .franchiseId("unknown")
                .build();

        when(franchiseRepository.findById("unknown")).thenReturn(Mono.empty());

        StepVerifier.create(branchService.addBranch(request))
                .expectError(FranchiseNotFoundException.class)
                .verify();

        verify(branchRepository, never()).save(any());
    }

    @Test
    @DisplayName("addBranch - branch name already exists in franchise - throws DuplicateNameException")
    void addBranch_whenNameDuplicated_throwsDuplicateNameException() {
        CreateBranchRequest request = CreateBranchRequest.builder()
                .name("Downtown")
                .franchiseId("f1")
                .build();
        Franchise franchise = Franchise.builder().id("f1").name("McDonald's").build();

        when(franchiseRepository.findById("f1")).thenReturn(Mono.just(franchise));
        when(branchRepository.existsByNameAndFranchiseId("Downtown", "f1")).thenReturn(Mono.just(true));

        StepVerifier.create(branchService.addBranch(request))
                .expectError(DuplicateNameException.class)
                .verify();

        verify(branchRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateBranchName - branch exists - returns updated response")
    void updateBranchName_whenExists_returnsUpdatedResponse() {
        String id = "b1";
        UpdateNameRequest request = UpdateNameRequest.builder().name("Uptown").build();
        Branch existing = Branch.builder().id(id).name("Downtown").franchiseId("f1").build();
        Branch updated = Branch.builder().id(id).name("Uptown").franchiseId("f1").build();

        when(branchRepository.findById(id)).thenReturn(Mono.just(existing));
        when(branchRepository.update(any(Branch.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(branchService.updateBranchName(id, request))
                .assertNext(response -> {
                    assertThat(response.getId()).isEqualTo(id);
                    assertThat(response.getName()).isEqualTo("Uptown");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("updateBranchName - branch not found - throws BranchNotFoundException")
    void updateBranchName_whenNotFound_throwsBranchNotFoundException() {
        String id = "nonexistent";
        UpdateNameRequest request = UpdateNameRequest.builder().name("Uptown").build();

        when(branchRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(branchService.updateBranchName(id, request))
                .expectError(BranchNotFoundException.class)
                .verify();

        verify(branchRepository, never()).update(any());
    }
}
