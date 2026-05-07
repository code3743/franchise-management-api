package dev.jotalopez.franchise_management_api.infrastructure.adapter.in.web;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateProductRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateStockRequest;
import dev.jotalopez.franchise_management_api.application.dto.response.ProductResponse;
import dev.jotalopez.franchise_management_api.application.dto.response.TopProductByBranchResponse;
import dev.jotalopez.franchise_management_api.application.port.in.ProductUseCase;
import dev.jotalopez.franchise_management_api.domain.exception.BranchNotFoundException;
import dev.jotalopez.franchise_management_api.domain.exception.ProductNotFoundException;
import dev.jotalopez.franchise_management_api.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ProductUseCase productUseCase;

    @Test
    @DisplayName("POST /api/products - valid request - returns 201")
    void create_withValidRequest_returns201() {
        ProductResponse response = ProductResponse.builder()
                .id("p1").name("Big Mac").stock(50).branchId("b1").build();

        when(productUseCase.addProduct(any())).thenReturn(Mono.just(response));

        webTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(CreateProductRequest.builder().name("Big Mac").stock(50).branchId("b1").build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("p1")
                .jsonPath("$.stock").isEqualTo(50);
    }

    @Test
    @DisplayName("POST /api/products - branch not found - returns 404")
    void create_whenBranchNotFound_returns404() {
        when(productUseCase.addProduct(any()))
                .thenReturn(Mono.error(new BranchNotFoundException("b1")));

        webTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(CreateProductRequest.builder().name("Big Mac").stock(50).branchId("b1").build())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("POST /api/products - negative stock - returns 400")
    void create_withNegativeStock_returns400() {
        webTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Big Mac\",\"stock\":-1,\"branchId\":\"b1\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - product exists - returns 204")
    void delete_whenExists_returns204() {
        when(productUseCase.deleteProduct("p1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/products/p1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - product not found - returns 404")
    void delete_whenNotFound_returns404() {
        when(productUseCase.deleteProduct("x"))
                .thenReturn(Mono.error(new ProductNotFoundException("x")));

        webTestClient.delete().uri("/api/products/x")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("PATCH /api/products/{id}/stock - product exists - returns 200 with new stock")
    void updateStock_whenExists_returns200() {
        ProductResponse response = ProductResponse.builder()
                .id("p1").name("Big Mac").stock(200).branchId("b1").build();

        when(productUseCase.updateStock(eq("p1"), any())).thenReturn(Mono.just(response));

        webTestClient.patch().uri("/api/products/p1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UpdateStockRequest.builder().stock(200).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stock").isEqualTo(200);
    }

    @Test
    @DisplayName("PATCH /api/products/{id}/stock - product not found - returns 404")
    void updateStock_whenNotFound_returns404() {
        when(productUseCase.updateStock(eq("x"), any()))
                .thenReturn(Mono.error(new ProductNotFoundException("x")));

        webTestClient.patch().uri("/api/products/x/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UpdateStockRequest.builder().stock(10).build())
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    @DisplayName("PATCH /api/products/{id}/name - product exists - returns 200 with new name")
    void updateName_whenExists_returns200() {
        ProductResponse response = ProductResponse.builder()
                .id("p1").name("McChicken").stock(50).branchId("b1").build();

        when(productUseCase.updateProductName(eq("p1"), any())).thenReturn(Mono.just(response));

        webTestClient.patch().uri("/api/products/p1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(UpdateNameRequest.builder().name("McChicken").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("McChicken");
    }

    @Test
    @DisplayName("GET /api/products/top-stock/franchise/{id} - returns top product per branch")
    void getTopByFranchise_returnsTopProductPerBranch() {
        ProductResponse product = ProductResponse.builder()
                .id("p1").name("Big Mac").stock(100).branchId("b1").build();
        TopProductByBranchResponse top = TopProductByBranchResponse.builder()
                .branchId("b1").branchName("Downtown").product(product).build();

        when(productUseCase.getTopProductPerBranch("f1")).thenReturn(Flux.just(top));

        webTestClient.get().uri("/api/products/top-stock/franchise/f1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].branchId").isEqualTo("b1")
                .jsonPath("$[0].branchName").isEqualTo("Downtown")
                .jsonPath("$[0].product.stock").isEqualTo(100);
    }

    @Test
    @DisplayName("GET /api/products/top-stock/franchise/{id} - no branches - returns empty array")
    void getTopByFranchise_whenNoBranches_returnsEmptyArray() {
        when(productUseCase.getTopProductPerBranch("empty")).thenReturn(Flux.empty());

        webTestClient.get().uri("/api/products/top-stock/franchise/empty")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isEmpty();
    }
}
