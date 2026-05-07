package dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.adapter;

import dev.jotalopez.franchise_management_api.domain.model.Product;
import dev.jotalopez.franchise_management_api.domain.port.out.ProductRepositoryPort;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.document.ProductDocument;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.mapper.ProductMapper;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.repository.ProductMongoRepository;
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
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final ProductMongoRepository mongoRepository;
    private final ReactiveMongoTemplate mongoTemplate;
    private final ProductMapper mapper;

    @Override
    public Mono<Product> save(Product product) {
        return mongoRepository.save(mapper.toDocument(product))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Product> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Product> findByBranchId(String branchId) {
        return mongoRepository.findByBranchId(branchId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }

    @Override
    public Mono<Product> update(Product product) {
        Query query = Query.query(Criteria.where("_id").is(product.getId()));
        Update update = new Update()
                .set("name", product.getName())
                .set("stock", product.getStock());
        return mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                ProductDocument.class
        ).map(mapper::toDomain);
    }

    @Override
    public Mono<Product> findTopByBranchIdOrderByStockDesc(String branchId) {
        return mongoRepository.findTopByBranchIdOrderByStockDesc(branchId)
                .map(mapper::toDomain);
    }
}
