package com.salesapp.enums;

public enum RoleEnum {
    CUSTOMER,
    ADMIN,
    AI;

    public boolean isAdminOrAI() {
        return this == ADMIN || this == AI;
    }

    public boolean isCustomer() {
        return this == CUSTOMER;
    }
}
