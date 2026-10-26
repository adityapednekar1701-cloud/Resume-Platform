package com.Application.PlatForm.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Application.PlatForm.Dto.LoginRequest;
import com.Application.PlatForm.Dto.RegisterRequest;
import com.Application.PlatForm.Model.Users;
import com.Application.PlatForm.Repo.UsersRepo;

@Service
public class UsersService {

	@Autowired
	private UsersRepo repo;

	@Autowired
	private JwtService jwtservice;

	@Autowired
	private AuthenticationManager authManager;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

	public Users getByUserName(String userName) {
		Users user = repo.findByUserName(userName);
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
		return user;
	}

	public void registerUser(RegisterRequest request) {
		if (repo.findByUserName(request.userName()) != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
		}
		if (repo.findByUserEmail(request.userEmail()) != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
		}

		// A new entity is built from the validated fields only, so a client can never
		// set an id
		Users user = new Users();
		user.setUserName(request.userName());
		user.setUserEmail(request.userEmail());
		user.setRole(request.role());
		user.setUserPassword(encoder.encode(request.userPassword()));
		repo.save(user);
	}

	public String login(LoginRequest request) {
		try {
			Authentication authenticate = authManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.userName(), request.userPassword()));

			if (authenticate.isAuthenticated()) {
				return jwtservice.generateToken(authenticate.getName());
			}
		} catch (AuthenticationException e) {
			// wrong username or password: fall through to a clean 401
		}
		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
	}
}