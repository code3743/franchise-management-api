package dev.jotalopez.franchise_management_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private String id;
    private String name;
    private Integer stock;
    private String branchId;
}
