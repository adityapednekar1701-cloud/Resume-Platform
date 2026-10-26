package com.Application.PlatForm.Dto;

import java.util.List;

import com.Application.PlatForm.Model.JobPosting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Every field is optional. A field that is left out keeps its current value.
// If title or description IS sent, it must contain at least one non-space character.
public record UpdateJobRequest(
		@Pattern(regexp = "(?s).*\\S.*", message = "must not be blank") @Size(max = 200) String title,
		@Pattern(regexp = "(?s).*\\S.*", message = "must not be blank") @Size(max = 5000) String description,
		List<@NotBlank String> requiredSkills, JobPosting.Status status) {
}