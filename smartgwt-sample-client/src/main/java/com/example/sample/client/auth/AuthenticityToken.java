package com.example.sample.client.auth;

/**
 * A simple name/value pair representing the current CSRF token
 */
final class AuthenticityToken {
	
	String name;
	String value;

	public AuthenticityToken(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}
}