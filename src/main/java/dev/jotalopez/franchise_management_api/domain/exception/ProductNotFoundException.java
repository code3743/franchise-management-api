package dev.jotalopez.franchise_management_api.domain.exception;

public class ProductNotFoundException extends DomainException {

    public ProductNotFoundException(String id) {
        super("Product not found with id: " + id);
    }
}
