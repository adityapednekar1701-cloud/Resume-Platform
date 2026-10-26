package com.Application.PlatForm.Dto;

import com.Application.PlatForm.Model.Users;

public record UserResponse(int userId, String userName, String userEmail, Users.Role role) {

	public static UserResponse from(Users user) {
		return new UserResponse(user.getUserId(), user.getUserName(), user.getUserEmail(), user.getRole());
	}
}