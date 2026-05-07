package uk.ac.qmul.digitalid.domain;

public enum OrganizationType {
    CENTRAL_AUTHORITY,
    IMMIGRATION_AUTHORITY,
    TAX_AUTHORITY,
    DRIVING_LICENCE_AUTHORITY,
    BANK_EMPLOYER;

    public boolean isCentralAuthority() {
        return this == CENTRAL_AUTHORITY;
    }
}
