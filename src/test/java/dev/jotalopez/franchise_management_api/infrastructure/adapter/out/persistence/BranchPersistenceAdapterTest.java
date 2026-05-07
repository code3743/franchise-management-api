package dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence;

import dev.jotalopez.franchise_management_api.domain.model.Branch;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.adapter.BranchPersistenceAdapter;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.document.BranchDocument;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.mapper.BranchMapper;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.repository.BranchMongoRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchPersistenceAdapterTest {

    @Mock private BranchMongoRepository mongoRepository;
    @Mock private ReactiveMongoTemplate mongoTemplate;
    @Mock private BranchMapper mapper;

    @InjectMocks
    private BranchPersistenceAdapter adapter;

    @Test
    @DisplayName("save - maps and persists branch")
    void save_mapsAndPersists() {
        Branch domain = Branch.builder().name("Downtown").franchiseId("f1").build();
        BranchDocument doc = BranchDocument.builder().name("Downtown").franchiseId("f1").build();
        BranchDocument saved = BranchDocument.builder().id("b1").name("Downtown").franchiseId("f1").build();
        Branch expected = Branch.builder().id("b1").name("Downtown").franchiseId("f1").build();

        when(mapper.toDocument(domain)).thenReturn(doc);
        when(mongoRepository.save(doc)).thenReturn(Mono.just(saved));
        when(mapper.toDomain(saved)).thenReturn(expected);

        StepVerifier.create(adapter.save(domain))
                .assertNext(b -> assertThat(b.getId()).isEqualTo("b1"))
                .verifyComplete();
    }

    @Test
    @DisplayName("findById - branch exists - returns domain")
    void findById_whenExists_returnsDomain() {
        BranchDocument doc = BranchDocument.builder().id("b1").name("Downtown").franchiseId("f1").build();
        Branch domain = Branch.builder().id("b1").name("Downtown").franchiseId("f1").build();

        when(mongoRepository.findById("b1")).thenReturn(Mono.just(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        StepVerifier.create(adapter.findById("b1"))
                .assertNext(b -> assertThat(b.getFranchiseId()).isEqualTo("f1"))
                .verifyComplete();
    }

    @Test
    @DisplayName("findById - branch not found - returns empty")
    void findById_whenNotFound_returnsEmpty() {
        when(mongoRepository.findById("x")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById("x"))
                .verifyComplete();
    }

    @Test
    @DisplayName("findByFranchiseId - returns all branches for franchise")
    void findByFranchiseId_returnsBranches() {
        BranchDocument doc1 = BranchDocument.builder().id("b1").name("Downtown").franchiseId("f1").build();
        BranchDocument doc2 = BranchDocument.builder().id("b2").name("Uptown").franchiseId("f1").build();
        Branch d1 = Branch.builder().id("b1").name("Downtown").franchiseId("f1").build();
        Branch d2 = Branch.builder().id("b2").name("Uptown").franchiseId("f1").build();

        when(mongoRepository.findByFranchiseId("f1")).thenReturn(Flux.just(doc1, doc2));
        when(mapper.toDomain(doc1)).thenReturn(d1);
        when(mapper.toDomain(doc2)).thenReturn(d2);

        StepVerifier.create(adapter.findByFranchiseId("f1"))
                .assertNext(b -> assertThat(b.getName()).isEqualTo("Downtown"))
                .assertNext(b -> assertThat(b.getName()).isEqualTo("Uptown"))
                .verifyComplete();
    }

    @Test
    @DisplayName("update - uses $set via ReactiveMongoTemplate")
    void update_usesSetOperation() {
        Branch domain = Branch.builder().id("b1").name("Uptown").franchiseId("f1").build();
        BranchDocument updatedDoc = BranchDocument.builder().id("b1").name("Uptown").franchiseId("f1").build();
        Branch expected = Branch.builder().id("b1").name("Uptown").franchiseId("f1").build();

        when(mongoTemplate.findAndModify(
                any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(BranchDocument.class)))
                .thenReturn(Mono.just(updatedDoc));
        when(mapper.toDomain(updatedDoc)).thenReturn(expected);

        StepVerifier.create(adapter.update(domain))
                .assertNext(b -> assertThat(b.getName()).isEqualTo("Uptown"))
                .verifyComplete();
    }

    @Test
    @DisplayName("existsByNameAndFranchiseId - delegates to repository")
    void existsByNameAndFranchiseId_delegatesToRepository() {
        when(mongoRepository.existsByNameAndFranchiseId("Downtown", "f1")).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.existsByNameAndFranchiseId("Downtown", "f1"))
                .assertNext(exists -> assertThat(exists).isFalse())
                .verifyComplete();
    }
}
