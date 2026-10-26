package com.Application.PlatForm.Dto;

import java.util.Date;

import com.Application.PlatForm.Model.Application;
import com.fasterxml.jackson.annotation.JsonInclude;

// Two views of the same application:
//   from(app)          -> the candidate's own view: status only, no score, no AI explanation, no email
//   forRecruiter(app)  -> the job owner's view: also includes score, explanation and the candidate's email
// A field that is null is left out of the JSON completely.
public record ApplicationResponse(int id, Application.ApplicationStatus status,
		@JsonInclude(JsonInclude.Include.NON_NULL) Double matchScore,
		@JsonInclude(JsonInclude.Include.NON_NULL) String aiExplanation, Date appliedAt, String resumeName, int jobId,
		String jobTitle, String candidateName, @JsonInclude(JsonInclude.Include.NON_NULL) String candidateEmail) {

	public static ApplicationResponse from(Application app) {
		return build(app, false);
	}

	public static ApplicationResponse forRecruiter(Application app) {
		return build(app, true);
	}

	private static ApplicationResponse build(Application app, boolean recruiterView) {
		int jobId = app.getJobPosting() == null ? 0 : app.getJobPosting().getId();
		String jobTitle = app.getJobPosting() == null ? null : app.getJobPosting().getTitle();
		String candidateName = app.getCandidate() == null ? null : app.getCandidate().getUserName();

		Double score = recruiterView ? app.getMatchScore() : null;
		String explanation = recruiterView ? app.getAiExplanation() : null;
		String email = (recruiterView && app.getCandidate() != null) ? app.getCandidate().getUserEmail() : null;

		return new ApplicationResponse(app.getId(), app.getStatus(), score, explanation, app.getAppliedAt(),
				app.getResumeName(), jobId, jobTitle, candidateName, email);
	}
}