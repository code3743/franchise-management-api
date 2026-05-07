package dev.jotalopez.franchise_management_api.infrastructure.adapter.in.web;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateFranchiseRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.application.dto.response.FranchiseResponse;
import dev.jotalopez.franchise_management_api.application.port.in.FranchiseUseCase;
import dev.jotalopez.franchise_management_api.domain.exception.DuplicateNameException;
import dev.jotalopez.franchise_management_api.domain.exception.FranchiseNotFoundException;
import dev.jotalopez.franchise_management_api.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(FranchiseController.class)
@Import(GlobalExceptionHandler.class)
class FranchiseControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FranchiseUseCase franchiseUseCase;

    // ─── POST /api/franchises ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/franchises - valid request - returns 201 with body")
    void create_withValidRequest_returns201() {
        FranchiseResponse response = FranchiseResponse.builder().id("f1").name("McDonald's").build();

        when(franchiseUseCase.createFranchise(any())).thenReturn(Mono.just(response));

        webTestClient.post().uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(CreateFranchiseRequest.builder().name("McDonald's").build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("f1")
                .jsonPath("$.name").isEqualTo("McDonald's");
    }

    @Test
    @DisplayName("POST /api/franchises - blank name - returns 400")
    void create_withBlankName_returns400() {
        webTestClient.post().uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /api/franchises - duplicate name - returns 409")
    void create_withDuplicateName_returns409() {
        when(franchiseUseCase.createFranchise(any()))
                .thenReturn(Mono.error(new DuplicateNameException("Franchise name already exists")));

        webTestClient.post().uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(CreateFranchiseRequest.builder().name("McDonald's").build())
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.message").isNotEmpty();
    }

    @Test
    @DisplayName("PATCH /api/franchises/{id}/name - franchise exists - returns 200")
    void updateName_whenExists_returns200() {
        FranchiseResponse response = FranchiseResponse.builder().id("f1").name("Burger King").build();

        when(franchiseUseCase.updateFranchiseName(eq("f1"), any())).thenReturn(Mono.just(response));

        webTestClient.patch().uri("/api/franchises/f1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UpdateNameRequest.builder().name("Burger King").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Burger King");
    }

    @Test
    @DisplayName("PATCH /api/franchises/{id}/name - franchise not found - returns 404")
    void updateName_whenNotFound_returns404() {
        when(franchiseUseCase.updateFranchiseName(eq("x"), any()))
                .thenReturn(Mono.error(new FranchiseNotFoundException("x")));

        webTestClient.patch().uri("/api/franchises/x/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UpdateNameRequest.builder().name("New Name").build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.path").isEqualTo("/api/franchises/x/name");
    }
}
