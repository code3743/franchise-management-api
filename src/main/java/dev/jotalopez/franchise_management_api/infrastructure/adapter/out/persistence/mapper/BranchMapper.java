package dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.mapper;

import dev.jotalopez.franchise_management_api.domain.model.Branch;
import dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.document.BranchDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchMapper {

    Branch toDomain(BranchDocument document);

    BranchDocument toDocument(Branch branch);
}
