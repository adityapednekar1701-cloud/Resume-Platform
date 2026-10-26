package com.Application.PlatForm.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Application.PlatForm.Model.Application;
import com.Application.PlatForm.Model.JobPosting;
import com.Application.PlatForm.Model.Users;

@Repository
public interface ApplicationRepo extends JpaRepository<Application, Integer> {

	List<Application> findByCandidate(Users candidate);

	boolean existsByCandidateAndJobPosting(Users candidate, JobPosting jobPosting);

	boolean existsByJobPosting(JobPosting jobPosting);

	// ApplicationRepo
	List<Application> findByJobPostingOrderByMatchScoreDesc(JobPosting jobPosting);
}