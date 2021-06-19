package com.pinus.alexdev.avis.dto.response.user_updated_response;

import java.util.List;

import lombok.Data;

@Data
public class UserUpdatedResponse {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String notificationState;
    private String avatarUrl;
    private List<NotificationTokens> notificationTokens;
    private String middleName;
    private String id;
    private String type;
    private String email;
    private String username;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNotificationState() {
        return notificationState;
    }

    public void setNotificationState(String notificationState) {
        this.notificationState = notificationState;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<NotificationTokens> getNotificationTokens() {
        return notificationTokens;
    }

    public void setNotificationTokens(List<NotificationTokens> notificationTokens) {
        this.notificationTokens = notificationTokens;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "ClassPojo [firstName = " + firstName + ", lastName = " + lastName + ", phoneNumber = " + phoneNumber + ", notificationState = " + notificationState + ", avatarUrl = " + avatarUrl + ", notificationTokens = " + notificationTokens + ", middleName = " + middleName + ", id = " + id + ", type = " + type + ", email = " + email + ", username = " + username + "]";
    }
}
