package dev.jotalopez.franchise_management_api.domain.exception;

public class DuplicateNameException extends DomainException {

    public DuplicateNameException(String message) {
        super(message);
    }
}
