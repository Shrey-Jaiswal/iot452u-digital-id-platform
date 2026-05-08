package uk.ac.qmul.digitalid.application;

public final class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) {
        super(message);
    }
}
