package com.Application.PlatForm.Dto;

import jakarta.validation.constraints.Positive;

public record ApplicationRequest(@Positive int jobId) {
}