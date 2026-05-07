package dev.jotalopez.franchise_management_api.application.service;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateFranchiseRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.domain.exception.DuplicateNameException;
import dev.jotalopez.franchise_management_api.domain.exception.FranchiseNotFoundException;
import dev.jotalopez.franchise_management_api.domain.model.Franchise;
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
class FranchiseServiceTest {

    @Mock
    private FranchiseRepositoryPort franchiseRepository;

    @InjectMocks
    private FranchiseService franchiseService;

    @Test
    @DisplayName("createFranchise - name is unique - returns saved franchise")
    void createFranchise_whenNameIsUnique_returnsResponse() {
        CreateFranchiseRequest request = CreateFranchiseRequest.builder().name("McDonald's").build();
        Franchise saved = Franchise.builder().id("f1").name("McDonald's").build();

        when(franchiseRepository.existsByName("McDonald's")).thenReturn(Mono.just(false));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(franchiseService.createFranchise(request))
                .assertNext(response -> {
                    assertThat(response.getId()).isEqualTo("f1");
                    assertThat(response.getName()).isEqualTo("McDonald's");
                })
                .verifyComplete();

        verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    @DisplayName("createFranchise - name already exists - throws DuplicateNameException")
    void createFranchise_whenNameExists_throwsDuplicateNameException() {
        CreateFranchiseRequest request = CreateFranchiseRequest.builder().name("McDonald's").build();

        when(franchiseRepository.existsByName("McDonald's")).thenReturn(Mono.just(true));

        StepVerifier.create(franchiseService.createFranchise(request))
                .expectError(DuplicateNameException.class)
                .verify();

        verify(franchiseRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateFranchiseName - franchise exists - returns updated response")
    void updateFranchiseName_whenExists_returnsUpdatedResponse() {
        String id = "f1";
        UpdateNameRequest request = UpdateNameRequest.builder().name("Burger King").build();
        Franchise existing = Franchise.builder().id(id).name("McDonald's").build();
        Franchise updated = Franchise.builder().id(id).name("Burger King").build();

        when(franchiseRepository.findById(id)).thenReturn(Mono.just(existing));
        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(franchiseService.updateFranchiseName(id, request))
                .assertNext(response -> {
                    assertThat(response.getId()).isEqualTo(id);
                    assertThat(response.getName()).isEqualTo("Burger King");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("updateFranchiseName - franchise not found - throws FranchiseNotFoundException")
    void updateFranchiseName_whenNotFound_throwsFranchiseNotFoundException() {
        String id = "nonexistent";
        UpdateNameRequest request = UpdateNameRequest.builder().name("New Name").build();

        when(franchiseRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseService.updateFranchiseName(id, request))
                .expectError(FranchiseNotFoundException.class)
                .verify();

        verify(franchiseRepository, never()).update(any());
    }
}
