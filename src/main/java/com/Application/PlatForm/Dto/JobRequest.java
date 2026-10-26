package com.Application.PlatForm.Dto;

import java.util.List;

import com.Application.PlatForm.Model.JobPosting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Used for creating a job. There is no id and no recruiter field, so a client cannot set either.
public record JobRequest(@NotBlank @Size(max = 200) String title, @NotBlank @Size(max = 5000) String description,
		List<@NotBlank String> requiredSkills, JobPosting.Status status) {
}