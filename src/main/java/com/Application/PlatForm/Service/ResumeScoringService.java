package com.Application.PlatForm.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ResumeScoringService {

	private final ChatClient chatClient;

	public ResumeScoringService(ChatClient.Builder builder) {
		this.chatClient = builder.build();
	}

	public record MatchResult(int score, String explanation) {
	}

	public MatchResult scoreResume(String resumeText, String jobDescription) {
		String prompt = """
				You are evaluating a candidate's resume against a job description.

				Job description:
				%s

				Resume:
				%s

				Respond with ONLY a valid JSON object in exactly this format, with no other text before or after it:
				{"score": <integer 0-100>, "explanation": "<brief explanation of strengths and gaps>"}
				""".formatted(jobDescription, resumeText);

		return chatClient.prompt(prompt).call().entity(MatchResult.class);
	}

}