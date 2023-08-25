package com.example.sample.client.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A simple name/value pair representing the current CSRF token
 */
@Data
@AllArgsConstructor
final class AuthenticityToken {

    private final String name;
    private final String value;

}