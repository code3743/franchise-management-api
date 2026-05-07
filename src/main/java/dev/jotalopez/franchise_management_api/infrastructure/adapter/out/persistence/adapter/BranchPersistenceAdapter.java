package dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.adapter;

import dev.jotalopez.franchise_management_api.domain.model.Branch;
import dev.jotalopez.franchise_management_api.domain.port.out.BranchRepositoryPort;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.document.BranchDocument;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.mapper.BranchMapper;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.repository.BranchMongoRepository;
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
public class BranchPersistenceAdapter implements BranchRepositoryPort {

    private final BranchMongoRepository mongoRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final BranchMapper mapper;

    @Override
    public Mono<Branch> save(Branch branch) {
        return mongoRepository.save(mapper.toDocument(branch))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Branch> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Branch> findByFranchiseId(String franchiseId) {
        return mongoRepository.findByFranchiseId(franchiseId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Branch> update(Branch branch) {
        Query query = Query.query(Criteria.where("_id").is(branch.getId()));
        Update update = new Update().set("name", branch.getName());
        return mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                BranchDocument.class
        ).map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByNameAndFranchiseId(String name, String franchiseId) {
        return mongoRepository.existsByNameAndFranchiseId(name, franchiseId);
    }
}
