package com.janex.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
