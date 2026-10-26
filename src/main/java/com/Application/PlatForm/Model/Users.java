package com.Application.PlatForm.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Users {

	@Id
	@GeneratedValue
	private int userId;

	@Column(unique = true)
	private String userName;

	@Column(unique = true)
	private String userEmail;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String userPassword;

	public enum Role {
		RECRUITER, CANDIDATE
	}

	@Enumerated(EnumType.STRING)
	private Role role;

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

}
