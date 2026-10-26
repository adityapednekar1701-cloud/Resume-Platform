package com.Application.PlatForm.Producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.Application.PlatForm.Model.Application;
import com.Application.PlatForm.Repo.ApplicationRepo;

@Service
public class ScoringProducer {

	public static final String TOPIC = "application.submitted";

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private ApplicationRepo repo;

	// The message is just the application id. The consumer loads the application
	// (and its resume) from the database, so no file bytes travel through Kafka.
	public void publishSubmitted(int applicationId) {
		String id = String.valueOf(applicationId);
		try {
			kafkaTemplate.send(TOPIC, id, id).whenComplete((result, ex) -> {
				if (ex != null) {
					markFailed(applicationId); // Kafka unreachable or send rejected
				}
			});
		} catch (Exception e) {
			markFailed(applicationId);
		}
	}

	// Without this, an application whose message never reached Kafka would sit on
	// SUBMITTED forever
	private void markFailed(int applicationId) {
		repo.findById(applicationId).ifPresent(app -> {
			if (app.getStatus() == Application.ApplicationStatus.SUBMITTED) {
				app.setStatus(Application.ApplicationStatus.FAILED);
				repo.save(app);
			}
		});
	}
}