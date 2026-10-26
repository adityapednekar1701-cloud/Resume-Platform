package com.Application.PlatForm.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Application.PlatForm.Dto.JobRequest;
import com.Application.PlatForm.Dto.JobResponse;
import com.Application.PlatForm.Dto.UpdateJobRequest;
import com.Application.PlatForm.Model.JobPosting;
import com.Application.PlatForm.Model.JobPosting.Status;
import com.Application.PlatForm.Service.JobService;

import jakarta.validation.Valid;

@RestController
public class JobController {

	@Autowired
	private JobService service;

	@GetMapping("/jobs")
	public ResponseEntity<List<JobResponse>> getJobs(@RequestParam(name = "status", required = false) Status status) {
		List<JobPosting> jobs = (status != null) ? service.findByStatus(status) : service.findAll();
		return ResponseEntity.ok(jobs.stream().map(JobResponse::from).toList());
	}

	@PostMapping("/jobs")
	public ResponseEntity<JobResponse> createJobPosting(@Valid @RequestBody JobRequest request, Authentication auth) {
		return new ResponseEntity<>(JobResponse.from(service.createJobPosting(request, auth)), HttpStatus.CREATED);
	}

	@GetMapping("/jobs/{id}")
	public ResponseEntity<JobResponse> getJob(@PathVariable int id) {
		JobPosting job = service.getJob(id);
		if (job == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(JobResponse.from(job));
	}

	@PutMapping("/jobs/{id}")
	public ResponseEntity<JobResponse> updateJob(@PathVariable int id, @Valid @RequestBody UpdateJobRequest request,
			Authentication auth) {
		return ResponseEntity.ok(JobResponse.from(service.updateJob(id, request, auth)));
	}

	@DeleteMapping("/jobs/{id}")
	public ResponseEntity<Void> deleteJob(@PathVariable int id, Authentication auth) {
		service.deleteJob(id, auth);
		return ResponseEntity.noContent().build();
	}
}