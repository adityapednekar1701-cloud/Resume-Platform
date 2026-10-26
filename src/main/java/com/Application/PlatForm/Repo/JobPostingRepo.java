package com.Application.PlatForm.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Application.PlatForm.Model.JobPosting;

@Repository
public interface JobPostingRepo extends JpaRepository<JobPosting, Integer> {
	List<JobPosting> findByStatus(JobPosting.Status status);
}
