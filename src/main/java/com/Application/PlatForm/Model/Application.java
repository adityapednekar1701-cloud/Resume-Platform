package com.Application.PlatForm.Model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "candidate_user_id", "job_posting_id" }))
public class Application {

	@Id
	@GeneratedValue
	private int id;

	@ManyToOne
	private Users candidate;

	@ManyToOne
	private JobPosting jobPosting;

	public enum ApplicationStatus {
		SUBMITTED, PROCESSING, SCORED, FAILED
	}

	@Nullable
	private Double matchScore;

	@Lob
	private String aiExplanation;

	private Date appliedAt;

	private String resumeName;
	private String resumeType;

	@Lob
	@JsonIgnore
	private byte[] resumeData;

	public String getResumeName() {
		return resumeName;
	}

	public String getAiExplanation() {
		return aiExplanation;
	}

	public void setAiExplanation(String aiExplanation) {
		this.aiExplanation = aiExplanation;
	}

	public void setResumeName(String resumeName) {
		this.resumeName = resumeName;
	}

	public String getResumeType() {
		return resumeType;
	}

	public void setResumeType(String resumeType) {
		this.resumeType = resumeType;
	}

	public byte[] getResumeData() {
		return resumeData;
	}

	public void setResumeData(byte[] resumeData) {
		this.resumeData = resumeData;
	}

	@Enumerated(EnumType.STRING)
	private ApplicationStatus status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Users getCandidate() {
		return candidate;
	}

	public void setCandidate(Users candidate) {
		this.candidate = candidate;
	}

	public JobPosting getJobPosting() {
		return jobPosting;
	}

	public void setJobPosting(JobPosting jobPosting) {
		this.jobPosting = jobPosting;
	}

	public Double getMatchScore() {
		return matchScore;
	}

	public void setMatchScore(Double matchScore) {
		this.matchScore = matchScore;
	}

	public Date getAppliedAt() {
		return appliedAt;
	}

	public void setAppliedAt(Date appliedAt) {
		this.appliedAt = appliedAt;
	}

	public ApplicationStatus getStatus() {
		return status;
	}

	public void setStatus(ApplicationStatus status) {
		this.status = status;
	}

}
