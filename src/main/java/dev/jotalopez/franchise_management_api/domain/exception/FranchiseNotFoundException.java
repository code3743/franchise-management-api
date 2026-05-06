package dev.jotalopez.franchise_management_api.domain.exception;

public class FranchiseNotFoundException extends DomainException {

    public FranchiseNotFoundException(String id) {
        super("Franchise not found with id: " + id);
    }
}
