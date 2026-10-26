package com.Application.PlatForm.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.Application.PlatForm.Model.Application;
import com.Application.PlatForm.Model.JobPosting;
import com.Application.PlatForm.Model.Users;
import com.Application.PlatForm.Producer.ScoringProducer;
import com.Application.PlatForm.Repo.ApplicationRepo;
import com.Application.PlatForm.Repo.JobPostingRepo;
import com.Application.PlatForm.Repo.UsersRepo;

@Service
public class ApplicationService {

	private static final String PDF_TYPE = "application/pdf";

	@Autowired
	private ApplicationRepo repo;

	@Autowired
	private UsersRepo usersRepo;

	@Autowired
	private JobPostingRepo jobRepo;

	@Autowired
	private ScoringProducer scoringProducer;

	public Application createApplication(String username, int jobId, MultipartFile resumeFile) {
		// The candidate always comes from the logged-in user, never from the request
		// body
		Users candidate = usersRepo.findByUserName(username);
		if (candidate == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
		}

		JobPosting job = jobRepo.findById(jobId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

		if (job.getStatus() == JobPosting.Status.CLOSED) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This job is closed");
		}

		if (repo.existsByCandidateAndJobPosting(candidate, job)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already applied to this job");
		}

		byte[] resumeBytes;
		try {
			resumeBytes = resumeFile.getBytes();
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read resume file");
		}

		// The content type sent by the client can be faked, so check the file's own
		// first bytes
		if (!isPdf(resumeBytes)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resume must be a PDF file");
		}

		Application app = new Application();
		app.setCandidate(candidate);
		app.setJobPosting(job);
		app.setResumeName(resumeFile.getOriginalFilename());
		app.setResumeType(PDF_TYPE); // fixed value, not client-supplied
		app.setResumeData(resumeBytes);
		app.setAppliedAt(new Date());
		app.setStatus(Application.ApplicationStatus.SUBMITTED);

		Application saved;
		try {
			// save() commits before it returns, so the consumer is guaranteed to find the
			// row
			saved = repo.save(app);
		} catch (DataIntegrityViolationException e) {
			// Two simultaneous requests can both pass the check above; the unique
			// constraint catches the second
			throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already applied to this job");
		}

		// Scoring happens asynchronously in ScoringConsumer. The caller gets an
		// immediate
		// response with status SUBMITTED and polls for SCORED / FAILED.
		scoringProducer.publishSubmitted(saved.getId());

		return saved;
	}

	// Only the candidate who applied, or the recruiter who owns the job, may see an
	// application
	public Application getApplicationForUser(int id, Authentication auth) {
		Application app = repo.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

		String user = auth.getName();
		boolean isCandidate = app.getCandidate() != null && app.getCandidate().getUserName().equals(user);

		JobPosting job = app.getJobPosting();
		boolean isJobOwner = job != null && job.getRecruiter() != null && job.getRecruiter().getUserName().equals(user);

		if (!isCandidate && !isJobOwner) {
			throw new AccessDeniedException("Not allowed to view this application");
		}
		return app;
	}

	public List<Application> getMyApplications(String username) {
		Users candidate = usersRepo.findByUserName(username);
		if (candidate == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
		}
		return repo.findByCandidate(candidate);
	}

	public List<Application> getApplicationsForJob(int jobId, Authentication auth) {
		JobPosting job = jobRepo.findById(jobId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
		if (job.getRecruiter() == null || !job.getRecruiter().getUserName().equals(auth.getName())) {
			throw new AccessDeniedException("Not your job posting");
		}
		return repo.findByJobPostingOrderByMatchScoreDesc(job);
	}

	private boolean isPdf(byte[] bytes) {
		return bytes.length > 4 && bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F';
	}
}