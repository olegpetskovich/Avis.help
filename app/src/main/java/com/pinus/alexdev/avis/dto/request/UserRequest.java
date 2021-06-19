package com.pinus.alexdev.avis.dto.request;

import lombok.Data;

@Data
public class UserRequest {
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String email;
	private String username;

	public UserRequest(String firstName, String lastName, String phoneNumber, String email, String username) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.username = username;
	}
}
