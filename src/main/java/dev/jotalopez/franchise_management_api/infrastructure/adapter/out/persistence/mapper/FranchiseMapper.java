package dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.mapper;

import dev.jotalopez.franchise_management_api.domain.model.Franchise;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.document.FranchiseDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {

    Franchise toDomain(FranchiseDocument document);

    FranchiseDocument toDocument(Franchise franchise);
}
