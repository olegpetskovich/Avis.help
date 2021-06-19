package com.pinus.alexdev.avis.dto.response.user_summary_response;

import com.google.gson.Gson;

import java.util.List;

import lombok.Data;

@Data
public class UserSummaryResponse{
	private String firstName;
	private String lastName;
	private String role;
	private String phoneNumber;
	private UserSummaryData data;
	private String avatarUrl;
	private List<String> permissions;
	private String middleName;
	private String id;
	private String email;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public UserSummaryData getData() {
		return data;
	}

	public void setData(UserSummaryData data) {
		this.data = data;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String serialize() {
		// Serialize this class into a JSON string using GSON
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	static public UserSummaryResponse create(String serializedData) {
		// Use GSON to instantiate this class using the JSON representation of the state
		Gson gson = new Gson();
		return gson.fromJson(serializedData, UserSummaryResponse.class);
	}
}
