package com.Application.PlatForm.Model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class JobPosting {

	@Id
	@GeneratedValue
	private int id;
	private String title;

	@Column(length = 5000)
	private String description;

	@ElementCollection
	private List<String> requiredSkills;

	@ManyToOne
	private Users recruiter;

	public enum Status {
		OPEN, CLOSED
	}

	private Date createdAt;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Enumerated(EnumType.STRING)
	private Status status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getRequiredSkills() {
		return requiredSkills;
	}

	public void setRequiredSkills(List<String> requiredSkills) {
		this.requiredSkills = requiredSkills;
	}

	public Users getRecruiter() {
		return recruiter;
	}

	public void setRecruiter(Users recruiter) {
		this.recruiter = recruiter;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
