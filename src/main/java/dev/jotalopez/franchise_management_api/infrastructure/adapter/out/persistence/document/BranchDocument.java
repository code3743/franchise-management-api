package dev.jotalopez.franchise_management_api.infrastructure.adapter.out.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "branches")
@CompoundIndex(name = "name_franchise_unique", def = "{'name': 1, 'franchiseId': 1}", unique = true)
public class BranchDocument {

    @Id
    private String id;

    private String name;

    private String franchiseId;
}
