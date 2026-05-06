package dev.jotalopez.franchise_management_api.domain.exception;

public class BranchNotFoundException extends DomainException {

    public BranchNotFoundException(String id) {
        super("Branch not found with id: " + id);
    }
}
