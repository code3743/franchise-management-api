package dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence;

import dev.jotalopez.franchise_management_api.domain.model.Franchise;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.adapter.FranchisePersistenceAdapter;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.document.FranchiseDocument;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.mapper.FranchiseMapper;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.repository.FranchiseMongoRepository;
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
class FranchisePersistenceAdapterTest {

    @Mock private FranchiseMongoRepository mongoRepository;
    @Mock private ReactiveMongoTemplate mongoTemplate;
    @Mock private FranchiseMapper mapper;

    @InjectMocks
    private FranchisePersistenceAdapter adapter;

    @Test
    @DisplayName("save - maps domain to document, saves, maps back to domain")
    void save_mapsAndPersists() {
        Franchise domain = Franchise.builder().name("McDonald's").build();
        FranchiseDocument doc = FranchiseDocument.builder().name("McDonald's").build();
        FranchiseDocument saved = FranchiseDocument.builder().id("f1").name("McDonald's").build();
        Franchise expected = Franchise.builder().id("f1").name("McDonald's").build();

        when(mapper.toDocument(domain)).thenReturn(doc);
        when(mongoRepository.save(doc)).thenReturn(Mono.just(saved));
        when(mapper.toDomain(saved)).thenReturn(expected);

        StepVerifier.create(adapter.save(domain))
                .assertNext(f -> assertThat(f.getId()).isEqualTo("f1"))
                .verifyComplete();
    }

    @Test
    @DisplayName("findById - document exists - returns mapped domain")
    void findById_whenExists_returnsDomain() {
        FranchiseDocument doc = FranchiseDocument.builder().id("f1").name("McDonald's").build();
        Franchise domain = Franchise.builder().id("f1").name("McDonald's").build();

        when(mongoRepository.findById("f1")).thenReturn(Mono.just(doc));
        when(mapper.toDomain(doc)).thenReturn(domain);

        StepVerifier.create(adapter.findById("f1"))
                .assertNext(f -> assertThat(f.getName()).isEqualTo("McDonald's"))
                .verifyComplete();
    }

    @Test
    @DisplayName("findById - document not found - returns empty")
    void findById_whenNotFound_returnsEmpty() {
        when(mongoRepository.findById("x")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById("x"))
                .verifyComplete();
    }

    @Test
    @DisplayName("findAll - returns all mapped franchises")
    void findAll_returnsMappedList() {
        FranchiseDocument doc1 = FranchiseDocument.builder().id("f1").name("McDonald's").build();
        FranchiseDocument doc2 = FranchiseDocument.builder().id("f2").name("KFC").build();
        Franchise d1 = Franchise.builder().id("f1").name("McDonald's").build();
        Franchise d2 = Franchise.builder().id("f2").name("KFC").build();

        when(mongoRepository.findAll()).thenReturn(Flux.just(doc1, doc2));
        when(mapper.toDomain(doc1)).thenReturn(d1);
        when(mapper.toDomain(doc2)).thenReturn(d2);

        StepVerifier.create(adapter.findAll())
                .assertNext(f -> assertThat(f.getId()).isEqualTo("f1"))
                .assertNext(f -> assertThat(f.getId()).isEqualTo("f2"))
                .verifyComplete();
    }

    @Test
    @DisplayName("update - uses $set via ReactiveMongoTemplate and returns updated domain")
    void update_usesSetOperationAndReturnsDomain() {
        Franchise domain = Franchise.builder().id("f1").name("Burger King").build();
        FranchiseDocument updatedDoc = FranchiseDocument.builder().id("f1").name("Burger King").build();
        Franchise expected = Franchise.builder().id("f1").name("Burger King").build();

        when(mongoTemplate.findAndModify(
                any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(FranchiseDocument.class)))
                .thenReturn(Mono.just(updatedDoc));
        when(mapper.toDomain(updatedDoc)).thenReturn(expected);

        StepVerifier.create(adapter.update(domain))
                .assertNext(f -> assertThat(f.getName()).isEqualTo("Burger King"))
                .verifyComplete();
    }

    @Test
    @DisplayName("existsByName - delegates to repository")
    void existsByName_delegatesToRepository() {
        when(mongoRepository.existsByName("McDonald's")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByName("McDonald's"))
                .assertNext(exists -> assertThat(exists).isTrue())
                .verifyComplete();
    }
}
