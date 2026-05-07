package dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.adapter;

import dev.jotalopez.franchise_management_api.domain.model.Franchise;
import dev.jotalopez.franchise_management_api.domain.port.out.FranchiseRepositoryPort;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.document.FranchiseDocument;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.mapper.FranchiseMapper;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.repository.FranchiseMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchisePersistenceAdapter implements FranchiseRepositoryPort {

    private final FranchiseMongoRepository mongoRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final FranchiseMapper mapper;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return mongoRepository.save(mapper.toDocument(franchise))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Franchise> findAll() {
        return mongoRepository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Franchise> update(Franchise franchise) {
        Query query = Query.query(Criteria.where("_id").is(franchise.getId()));
        Update update = new Update().set("name", franchise.getName());
        return mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                FranchiseDocument.class
        ).map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return mongoRepository.existsByName(name);
    }
}
