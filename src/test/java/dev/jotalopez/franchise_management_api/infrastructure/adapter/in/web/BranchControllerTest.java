package dev.jotalopez.franchise_management_api.infrastructure.adapter.in.web;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateBranchRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.application.dto.response.BranchResponse;
import dev.jotalopez.franchise_management_api.application.port.in.BranchUseCase;
import dev.jotalopez.franchise_management_api.domain.exception.BranchNotFoundException;
import dev.jotalopez.franchise_management_api.domain.exception.DuplicateNameException;
import dev.jotalopez.franchise_management_api.domain.exception.FranchiseNotFoundException;
import dev.jotalopez.franchise_management_api.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(BranchController.class)
@Import(GlobalExceptionHandler.class)
class BranchControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BranchUseCase branchUseCase;

    @Test
    @DisplayName("POST /api/branches - valid request - returns 201 with body")
    void create_withValidRequest_returns201() {
        BranchResponse response = BranchResponse.builder()
                .id("b1").name("Downtown").franchiseId("f1").build();

        when(branchUseCase.addBranch(any())).thenReturn(Mono.just(response));

        webTestClient.post().uri("/api/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(CreateBranchRequest.builder().name("Downtown").franchiseId("f1").build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("b1")
                .jsonPath("$.franchiseId").isEqualTo("f1");
    }

    @Test
    @DisplayName("POST /api/branches - franchise not found - returns 404")
    void create_whenFranchiseNotFound_returns404() {
        when(branchUseCase.addBranch(any()))
                .thenReturn(Mono.error(new FranchiseNotFoundException("f1")));

        webTestClient.post().uri("/api/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(CreateBranchRequest.builder().name("Downtown").franchiseId("f1").build())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("POST /api/branches - duplicate branch name in franchise - returns 409")
    void create_withDuplicateName_returns409() {
        when(branchUseCase.addBranch(any()))
                .thenReturn(Mono.error(new DuplicateNameException("Branch name already exists")));

        webTestClient.post().uri("/api/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(CreateBranchRequest.builder().name("Downtown").franchiseId("f1").build())
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    @DisplayName("POST /api/branches - missing franchiseId - returns 400")
    void create_withMissingFranchiseId_returns400() {
        webTestClient.post().uri("/api/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Downtown\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("PATCH /api/branches/{id}/name - branch exists - returns 200")
    void updateName_whenExists_returns200() {
        BranchResponse response = BranchResponse.builder()
                .id("b1").name("Uptown").franchiseId("f1").build();

        when(branchUseCase.updateBranchName(eq("b1"), any())).thenReturn(Mono.just(response));

        webTestClient.patch().uri("/api/branches/b1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UpdateNameRequest.builder().name("Uptown").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Uptown");
    }

    @Test
    @DisplayName("PATCH /api/branches/{id}/name - branch not found - returns 404")
    void updateName_whenNotFound_returns404() {
        when(branchUseCase.updateBranchName(eq("x"), any()))
                .thenReturn(Mono.error(new BranchNotFoundException("x")));

        webTestClient.patch().uri("/api/branches/x/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UpdateNameRequest.builder().name("Uptown").build())
                .exchange()
                .expectStatus().isNotFound();
    }
}
