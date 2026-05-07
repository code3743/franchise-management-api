package dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.mapper;

import dev.jotalopez.franchise_management_api.domain.model.Product;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.document.ProductDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toDomain(ProductDocument document);

    ProductDocument toDocument(Product product);
}
