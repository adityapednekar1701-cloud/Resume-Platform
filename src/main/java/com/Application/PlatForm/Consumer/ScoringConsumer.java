package com.Application.PlatForm.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.Application.PlatForm.Model.Application;
import com.Application.PlatForm.Producer.ScoringProducer;
import com.Application.PlatForm.Repo.ApplicationRepo;
import com.Application.PlatForm.Service.ResumeScoringService;
import com.Application.PlatForm.Service.ResumeTextExtractor;

@Component
public class ScoringConsumer {

	private static final Logger log = LoggerFactory.getLogger(ScoringConsumer.class);

	@Autowired
	private ApplicationRepo repo;

	@Autowired
	private ResumeTextExtractor textExtractor;

	@Autowired
	private ResumeScoringService scoringService;

	@KafkaListener(topics = ScoringProducer.TOPIC, groupId = "resume-scoring-group")
	public void onSubmitted(String applicationId) {
		int id;
		try {
			id = Integer.parseInt(applicationId.trim());
		} catch (NumberFormatException | NullPointerException e) {
			log.error("Ignoring malformed message payload: {}", applicationId, e);
			return; // a bad message must not be retried forever
		}

		Application app = repo.findById(id).orElse(null);

		// Idempotency guard: Only process SUBMITTED applications
		if (app == null || app.getStatus() != Application.ApplicationStatus.SUBMITTED) {
			log.info("Skipping application ID {}: Not found or already processed", id);
			return;
		}

		app.setStatus(Application.ApplicationStatus.PROCESSING);
		repo.save(app);
		log.info("Application ID {} status changed to PROCESSING", id);

		try {
			String resumeText = textExtractor.extractText(app.getResumeData());

			// Defend against empty or scanned PDFs
			if (resumeText == null || resumeText.isBlank()) {
				log.warn("Application ID {}: No readable text found in PDF (possible scanned image)", id);
				app.setAiExplanation("No readable text found in this PDF (it may be a scanned image).");
				app.setStatus(Application.ApplicationStatus.FAILED);
				repo.save(app);
				return;
			}

			// Defend against context window overflow
			if (resumeText.length() > 12000) {
				log.info("Application ID {}: Truncating resume text from {} down to 12000 characters", id,
						resumeText.length());
				resumeText = resumeText.substring(0, 12000);
			}

			log.info("Sending application ID {} to AI scoring engine...", id);
			ResumeScoringService.MatchResult result = scoringService.scoreResume(resumeText,
					app.getJobPosting().getDescription());

			app.setMatchScore((double) result.score());
			app.setAiExplanation(result.explanation());
			app.setStatus(Application.ApplicationStatus.SCORED);
			log.info("Application ID {} successfully evaluated with match score: {}", id, result.score());
		} catch (Exception e) {
			log.error("System pipeline execution error processing application ID {}", id, e);
			app.setStatus(Application.ApplicationStatus.FAILED);
		}

		repo.save(app);
	}
}
