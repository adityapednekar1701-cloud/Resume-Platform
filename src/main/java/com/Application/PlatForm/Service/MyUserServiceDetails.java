package com.Application.PlatForm.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Application.PlatForm.Model.Users;
import com.Application.PlatForm.Repo.UsersRepo;



@Service
public class MyUserServiceDetails implements UserDetailsService {

	@Autowired
	private UsersRepo repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = repo.findByUserName(username);

		if (user == null) {
			throw new UsernameNotFoundException("UserName not found");
		}

		return new UserPrincipal(user);
	}

}
