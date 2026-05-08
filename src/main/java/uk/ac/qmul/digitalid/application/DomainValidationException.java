package uk.ac.qmul.digitalid.application;

public final class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
    }
}
