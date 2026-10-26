package com.Application.PlatForm.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Application.PlatForm.Dto.LoginRequest;
import com.Application.PlatForm.Dto.RegisterRequest;
import com.Application.PlatForm.Dto.UserResponse;
import com.Application.PlatForm.Service.UsersService;

import jakarta.validation.Valid;

@RestController
public class UsersController {

	@Autowired
	private UsersService service;

	@PostMapping("/register")
	public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
		service.registerUser(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/login")
	public String login(@Valid @RequestBody LoginRequest request) {
		return service.login(request);
	}

	// A user can only ever read their own profile
	@GetMapping("/users/me")
	public ResponseEntity<UserResponse> me(Authentication auth) {
		return ResponseEntity.ok(UserResponse.from(service.getByUserName(auth.getName())));
	}
}