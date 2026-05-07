package dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence;

import dev.jotalopez.franchise_management_api.domain.model.Product;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.adapter.ProductPersistenceAdapter;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.document.ProductDocument;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.mapper.ProductMapper;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.repository.ProductMongoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductPersistenceAdapterTest {

    @Mock private ProductMongoRepository mongoRepository;
    @Mock private ReactiveMongoTemplate mongoTemplate;
    @Mock private ProductMapper mapper;

    @InjectMocks
    private ProductPersistenceAdapter adapter;

    @Test
    @DisplayName("save - maps and persists product")
    void save_mapsAndPersists() {
        Product domain = Product.builder().name("Big Mac").stock(50).branchId("b1").build();
        ProductDocument doc = ProductDocument.builder().name("Big Mac").stock(50).branchId("b1").build();
        ProductDocument saved = ProductDocument.builder().id("p1").name("Big Mac").stock(50).branchId("b1").build();
        Product expected = Product.builder().id("p1").name("Big Mac").stock(50).branchId("b1").build();

        when(mapper.toDocument(domain)).thenReturn(doc);
        when(mongoRepository.save(doc)).thenReturn(Mono.just(saved));
        when(mapper.toDomain(saved)).thenReturn(expected);

        StepVerifier.create(adapter.save(domain))
                .assertNext(p -> assertThat(p.getId()).isEqualTo("p1"))
                .verifyComplete();
    }

    @Test
    @DisplayName("findById - product exists - returns domain")
    void findById_whenExists_returnsDomain() {
        ProductDocument doc = ProductDocument.builder().id("p1").name("Big Mac").stock(50).branchId("b1").build();
        Product domain = Product.builder().id("p1").name("Big Mac").stock(50).branchId("b1").build();

        when(mongoRepository.findById("p1")).thenReturn(Mono.just(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        StepVerifier.create(adapter.findById("p1"))
                .assertNext(p -> assertThat(p.getStock()).isEqualTo(50))
                .verifyComplete();
    }

    @Test
    @DisplayName("findById - product not found - returns empty")
    void findById_whenNotFound_returnsEmpty() {
        when(mongoRepository.findById("x")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById("x"))
                .verifyComplete();
    }

    @Test
    @DisplayName("findByBranchId - returns all products for branch")
    void findByBranchId_returnsProducts() {
        ProductDocument doc1 = ProductDocument.builder().id("p1").name("Big Mac").stock(50).branchId("b1").build();
        ProductDocument doc2 = ProductDocument.builder().id("p2").name("McChicken").stock(30).branchId("b1").build();
        Product d1 = Product.builder().id("p1").name("Big Mac").stock(50).branchId("b1").build();
        Product d2 = Product.builder().id("p2").name("McChicken").stock(30).branchId("b1").build();

        when(mongoRepository.findByBranchId("b1")).thenReturn(Flux.just(doc1, doc2));
        when(mapper.toDomain(doc1)).thenReturn(d1);
        when(mapper.toDomain(doc2)).thenReturn(d2);

        StepVerifier.create(adapter.findByBranchId("b1"))
                .assertNext(p -> assertThat(p.getStock()).isEqualTo(50))
                .assertNext(p -> assertThat(p.getStock()).isEqualTo(30))
                .verifyComplete();
    }

    @Test
    @DisplayName("deleteById - delegates to repository")
    void deleteById_delegatesToRepository() {
        when(mongoRepository.deleteById("p1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById("p1"))
                .verifyComplete();

        verify(mongoRepository).deleteById("p1");
    }

    @Test
    @DisplayName("update - uses $set for name and stock via ReactiveMongoTemplate")
    void update_usesSetForNameAndStock() {
        Product domain = Product.builder().id("p1").name("McChicken").stock(200).branchId("b1").build();
        ProductDocument updatedDoc = ProductDocument.builder().id("p1").name("McChicken").stock(200).branchId("b1").build();
        Product expected = Product.builder().id("p1").name("McChicken").stock(200).branchId("b1").build();

        when(mongoTemplate.findAndModify(
                any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(ProductDocument.class)))
                .thenReturn(Mono.just(updatedDoc));
        when(mapper.toDomain(updatedDoc)).thenReturn(expected);

        StepVerifier.create(adapter.update(domain))
                .assertNext(p -> {
                    assertThat(p.getName()).isEqualTo("McChicken");
                    assertThat(p.getStock()).isEqualTo(200);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("findTopByBranchIdOrderByStockDesc - returns product with highest stock")
    void findTopByBranchIdOrderByStockDesc_returnsTopProduct() {
        ProductDocument doc = ProductDocument.builder().id("p1").name("Big Mac").stock(100).branchId("b1").build();
        Product domain = Product.builder().id("p1").name("Big Mac").stock(100).branchId("b1").build();

        when(mongoRepository.findTopByBranchIdOrderByStockDesc("b1")).thenReturn(Mono.just(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        StepVerifier.create(adapter.findTopByBranchIdOrderByStockDesc("b1"))
                .assertNext(p -> assertThat(p.getStock()).isEqualTo(100))
                .verifyComplete();
    }

    @Test
    @DisplayName("findTopByBranchIdOrderByStockDesc - no products - returns empty")
    void findTopByBranchIdOrderByStockDesc_whenEmpty_returnsEmpty() {
        when(mongoRepository.findTopByBranchIdOrderByStockDesc("b1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findTopByBranchIdOrderByStockDesc("b1"))
                .verifyComplete();
    }
}
