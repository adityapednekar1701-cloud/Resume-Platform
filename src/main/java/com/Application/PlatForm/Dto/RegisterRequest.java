package com.Application.PlatForm.Dto;

import com.Application.PlatForm.Model.Users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank @Size(min = 3, max = 50) String userName, @NotBlank @Email String userEmail,
		@NotBlank @Size(min = 8, max = 72) String userPassword, // 72 = BCrypt's limit
		@NotNull Users.Role role) {
}