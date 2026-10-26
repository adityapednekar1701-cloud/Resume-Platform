package com.Application.PlatForm.Dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.Application.PlatForm.Model.JobPosting;

// Only the recruiter's name is exposed, never their email.
public record JobResponse(int id, String title, String description, List<String> requiredSkills,
		JobPosting.Status status, Date createdAt, String recruiterName) {

	public static JobResponse from(JobPosting job) {
		List<String> skills = job.getRequiredSkills() == null ? List.of() : new ArrayList<>(job.getRequiredSkills());
		String recruiter = job.getRecruiter() == null ? null : job.getRecruiter().getUserName();
		return new JobResponse(job.getId(), job.getTitle(), job.getDescription(), skills, job.getStatus(),
				job.getCreatedAt(), recruiter);
	}
}