package com.Application.PlatForm.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Application.PlatForm.Dto.ApplicationRequest;
import com.Application.PlatForm.Dto.ApplicationResponse;
import com.Application.PlatForm.Model.Application;
import com.Application.PlatForm.Service.ApplicationService;

import jakarta.validation.Valid;

@RestController
public class ApplicationController {

	@Autowired
	private ApplicationService service;

	@PostMapping("/applications")
	public ResponseEntity<ApplicationResponse> apply(@Valid @RequestPart ApplicationRequest request,
			@RequestPart MultipartFile resumeFile, Authentication auth) {
		Application application = service.createApplication(auth.getName(), request.jobId(), resumeFile);
		return new ResponseEntity<>(ApplicationResponse.from(application), HttpStatus.CREATED);
	}

	@GetMapping("/applications/me")
	public ResponseEntity<List<ApplicationResponse>> getMyApplications(Authentication auth) {
		return ResponseEntity
				.ok(service.getMyApplications(auth.getName()).stream().map(ApplicationResponse::from).toList());
	}

	@GetMapping("/applications/{id}")
	public ResponseEntity<ApplicationResponse> getApplication(@PathVariable int id, Authentication auth) {
		Application app = service.getApplicationForUser(id, auth);
		boolean isCandidate = app.getCandidate() != null && app.getCandidate().getUserName().equals(auth.getName());
		// The candidate sees their own application; the job's recruiter also gets the
		// candidate's email
		return ResponseEntity.ok(isCandidate ? ApplicationResponse.from(app) : ApplicationResponse.forRecruiter(app));
	}

	@GetMapping("/applications/{id}/resume")
	public ResponseEntity<byte[]> getResume(@PathVariable int id, Authentication auth) {
		Application application = service.getApplicationForUser(id, auth);

		if (application.getResumeData() == null) {
			return ResponseEntity.notFound().build();
		}

		// Only validated PDFs are ever stored, so the type is fixed rather than
		// client-supplied
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(application.getResumeData());
	}

	@GetMapping("/jobs/{jobId}/applications")
	public ResponseEntity<List<ApplicationResponse>> getApplicationsForJob(@PathVariable int jobId,
			Authentication auth) {
		return ResponseEntity.ok(
				service.getApplicationsForJob(jobId, auth).stream().map(ApplicationResponse::forRecruiter).toList());
	}
}