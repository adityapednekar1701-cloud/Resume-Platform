package com.Application.PlatForm.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Application.PlatForm.Model.Users;

@Repository
public interface UsersRepo extends JpaRepository<Users, Integer> {
	public Users findByUserEmail(String email);

	public Users findByUserName(String username);
}
