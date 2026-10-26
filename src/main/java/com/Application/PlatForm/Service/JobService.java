package com.Application.PlatForm.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.Application.PlatForm.Dto.JobRequest;
import com.Application.PlatForm.Dto.UpdateJobRequest;
import com.Application.PlatForm.Model.JobPosting;
import com.Application.PlatForm.Model.JobPosting.Status;
import com.Application.PlatForm.Model.Users;
import com.Application.PlatForm.Repo.ApplicationRepo;
import com.Application.PlatForm.Repo.JobPostingRepo;
import com.Application.PlatForm.Repo.UsersRepo;

@Service
public class JobService {

	@Autowired
	private JobPostingRepo repo;

	@Autowired
	private UsersRepo usersRepo;

	@Autowired
	private ApplicationRepo applicationRepo;

	public JobPosting createJobPosting(JobRequest request, Authentication auth) {
		Users recruiter = usersRepo.findByUserName(auth.getName());

		// Built from the validated request, so id and recruiter can never come from the
		// client
		JobPosting job = new JobPosting();
		job.setTitle(request.title());
		job.setDescription(request.description());
		job.setRequiredSkills(
				request.requiredSkills() == null ? new ArrayList<>() : new ArrayList<>(request.requiredSkills()));
		job.setStatus(request.status() == null ? Status.OPEN : request.status());
		job.setRecruiter(recruiter);
		job.setCreatedAt(new Date());
		return repo.save(job);
	}

	public List<JobPosting> findByStatus(Status status) {
		return repo.findByStatus(status);
	}

	public List<JobPosting> findAll() {
		return repo.findAll();
	}

	public JobPosting getJob(int id) {
		return repo.findById(id).orElse(null);
	}

	public JobPosting updateJob(int id, UpdateJobRequest request, Authentication auth) {
		JobPosting existing = getOwnedJob(id, auth);

		// Fields left out of the request keep their current value
		if (request.title() != null) {
			existing.setTitle(request.title());
		}
		if (request.description() != null) {
			existing.setDescription(request.description());
		}
		if (request.requiredSkills() != null) {
			existing.setRequiredSkills(new ArrayList<>(request.requiredSkills()));
		}
		if (request.status() != null) {
			existing.setStatus(request.status());
		}
		return repo.save(existing);
	}

	public void deleteJob(int id, Authentication auth) {
		JobPosting existing = getOwnedJob(id, auth);

		if (applicationRepo.existsByJobPosting(existing)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"This job has applications. Set its status to CLOSED instead of deleting it.");
		}
		repo.delete(existing);
	}

	// Loads the job (404 if missing) and checks that the logged-in user is its
	// recruiter (403 if not)
	private JobPosting getOwnedJob(int id, Authentication auth) {
		JobPosting job = repo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

		if (job.getRecruiter() == null || !job.getRecruiter().getUserName().equals(auth.getName())) {
			throw new AccessDeniedException("Not your job posting");
		}
		return job;
	}
}