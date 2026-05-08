package uk.ac.qmul.digitalid.rules;

public final class VerificationResult {
    private final boolean allowed;
    private final String reason;

    private VerificationResult(boolean allowed, String reason) {
        this.allowed = allowed;
        this.reason = reason;
    }

    public static VerificationResult allow() {
        return new VerificationResult(true, "OK");
    }

    public static VerificationResult deny(String reason) {
        return new VerificationResult(false, reason);
    }

    public boolean isAllowed() {
        return allowed;
    }

    public String getReason() {
        return reason;
    }
}
