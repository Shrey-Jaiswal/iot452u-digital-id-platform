package uk.ac.qmul.digitalid.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import uk.ac.qmul.digitalid.domain.OrganizationType;

class AuthorizationServiceTest {
    @Test
    void allowsCentralAuthority() {
        AuthorizationService service = new AuthorizationService();
        assertDoesNotThrow(() -> service.requireCentralAuthority(OrganizationType.CENTRAL_AUTHORITY));
    }

    @Test
    void rejectsNonCentralAuthority() {
        AuthorizationService service = new AuthorizationService();
        assertThrows(AuthorizationException.class,
                () -> service.requireCentralAuthority(OrganizationType.BANK_EMPLOYER));
    }
}
