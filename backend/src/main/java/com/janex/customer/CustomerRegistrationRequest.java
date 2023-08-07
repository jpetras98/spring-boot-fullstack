package com.janex.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}
