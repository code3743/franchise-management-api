package dev.jotalopez.franchise_management_api.application.service;

import dev.jotalopez.franchise_management_api.application.dto.request.CreateProductRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateNameRequest;
import dev.jotalopez.franchise_management_api.application.dto.request.UpdateStockRequest;
import dev.jotalopez.franchise_management_api.application.dto.response.TopProductByBranchResponse;
import dev.jotalopez.franchise_management_api.domain.exception.BranchNotFoundException;
import dev.jotalopez.franchise_management_api.domain.exception.ProductNotFoundException;
import dev.jotalopez.franchise_management_api.domain.model.Branch;
import dev.jotalopez.franchise_management_api.domain.model.Product;
import dev.jotalopez.franchise_management_api.domain.port.out.BranchRepositoryPort;
import dev.jotalopez.franchise_management_api.domain.port.out.ProductRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepository;

    @Mock
    private BranchRepositoryPort branchRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("addProduct - branch exists - returns saved product")
    void addProduct_whenBranchExists_returnsResponse() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Big Mac").stock(50).branchId("b1").build();
        Branch branch = Branch.builder().id("b1").name("Downtown").franchiseId("f1").build();
        Product saved = Product.builder().id("p1").name("Big Mac").stock(50).branchId("b1").build();

        when(branchRepository.findById("b1")).thenReturn(Mono.just(branch));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(productService.addProduct(request))
                .assertNext(response -> {
                    assertThat(response.getId()).isEqualTo("p1");
                    assertThat(response.getName()).isEqualTo("Big Mac");
                    assertThat(response.getStock()).isEqualTo(50);
                    assertThat(response.getBranchId()).isEqualTo("b1");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("addProduct - branch not found - throws BranchNotFoundException")
    void addProduct_whenBranchNotFound_throwsBranchNotFoundException() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Big Mac").stock(50).branchId("unknown").build();

        when(branchRepository.findById("unknown")).thenReturn(Mono.empty());

        StepVerifier.create(productService.addProduct(request))
                .expectError(BranchNotFoundException.class)
                .verify();

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteProduct - product exists - completes successfully")
    void deleteProduct_whenExists_completesSuccessfully() {
        String id = "p1";
        Product product = Product.builder().id(id).name("Big Mac").stock(50).branchId("b1").build();

        when(productRepository.findById(id)).thenReturn(Mono.just(product));
        when(productRepository.deleteById(id)).thenReturn(Mono.empty());

        StepVerifier.create(productService.deleteProduct(id))
                .verifyComplete();

        verify(productRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteProduct - product not found - throws ProductNotFoundException")
    void deleteProduct_whenNotFound_throwsProductNotFoundException() {
        String id = "nonexistent";

        when(productRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(productService.deleteProduct(id))
                .expectError(ProductNotFoundException.class)
                .verify();

        verify(productRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("updateStock - product exists - returns updated response")
    void updateStock_whenExists_returnsUpdatedResponse() {
        String id = "p1";
        UpdateStockRequest request = UpdateStockRequest.builder().stock(200).build();
        Product existing = Product.builder().id(id).name("Big Mac").stock(50).branchId("b1").build();
        Product updated = Product.builder().id(id).name("Big Mac").stock(200).branchId("b1").build();

        when(productRepository.findById(id)).thenReturn(Mono.just(existing));
        when(productRepository.update(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(productService.updateStock(id, request))
                .assertNext(response -> {
                    assertThat(response.getStock()).isEqualTo(200);
                    assertThat(response.getName()).isEqualTo("Big Mac");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("updateStock - product not found - throws ProductNotFoundException")
    void updateStock_whenNotFound_throwsProductNotFoundException() {
        when(productRepository.findById("x")).thenReturn(Mono.empty());

        StepVerifier.create(productService.updateStock("x", UpdateStockRequest.builder().stock(10).build()))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("updateProductName - product exists - returns updated response")
    void updateProductName_whenExists_returnsUpdatedResponse() {
        String id = "p1";
        UpdateNameRequest request = UpdateNameRequest.builder().name("McChicken").build();
        Product existing = Product.builder().id(id).name("Big Mac").stock(50).branchId("b1").build();
        Product updated = Product.builder().id(id).name("McChicken").stock(50).branchId("b1").build();

        when(productRepository.findById(id)).thenReturn(Mono.just(existing));
        when(productRepository.update(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(productService.updateProductName(id, request))
                .assertNext(response -> assertThat(response.getName()).isEqualTo("McChicken"))
                .verifyComplete();
    }

    @Test
    @DisplayName("updateProductName - product not found - throws ProductNotFoundException")
    void updateProductName_whenNotFound_throwsProductNotFoundException() {
        when(productRepository.findById("x")).thenReturn(Mono.empty());

        StepVerifier.create(productService.updateProductName("x", UpdateNameRequest.builder().name("X").build()))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("getTopProductPerBranch - franchise has branches with products - returns top per branch")
    void getTopProductPerBranch_returnTopProductForEachBranch() {
        String franchiseId = "f1";
        Branch b1 = Branch.builder().id("b1").name("Downtown").franchiseId(franchiseId).build();
        Branch b2 = Branch.builder().id("b2").name("Uptown").franchiseId(franchiseId).build();
        Product topB1 = Product.builder().id("p1").name("Big Mac").stock(100).branchId("b1").build();
        Product topB2 = Product.builder().id("p2").name("McChicken").stock(40).branchId("b2").build();

        when(branchRepository.findByFranchiseId(franchiseId)).thenReturn(Flux.just(b1, b2));
        when(productRepository.findTopByBranchIdOrderByStockDesc("b1")).thenReturn(Mono.just(topB1));
        when(productRepository.findTopByBranchIdOrderByStockDesc("b2")).thenReturn(Mono.just(topB2));

        StepVerifier.create(productService.getTopProductPerBranch(franchiseId).collectList())
                .assertNext(list -> {
                    assertThat(list).hasSize(2);

                    TopProductByBranchResponse forB1 = list.stream()
                            .filter(r -> r.getBranchId().equals("b1")).findFirst().orElseThrow();
                    assertThat(forB1.getBranchName()).isEqualTo("Downtown");
                    assertThat(forB1.getProduct().getStock()).isEqualTo(100);

                    TopProductByBranchResponse forB2 = list.stream()
                            .filter(r -> r.getBranchId().equals("b2")).findFirst().orElseThrow();
                    assertThat(forB2.getBranchName()).isEqualTo("Uptown");
                    assertThat(forB2.getProduct().getStock()).isEqualTo(40);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("getTopProductPerBranch - branch has no products - branch is omitted from result")
    void getTopProductPerBranch_whenBranchHasNoProducts_branchIsOmitted() {
        String franchiseId = "f1";
        Branch branch = Branch.builder().id("b1").name("Empty Branch").franchiseId(franchiseId).build();

        when(branchRepository.findByFranchiseId(franchiseId)).thenReturn(Flux.just(branch));
        when(productRepository.findTopByBranchIdOrderByStockDesc("b1")).thenReturn(Mono.empty());

        StepVerifier.create(productService.getTopProductPerBranch(franchiseId))
                .verifyComplete();
    }
}
