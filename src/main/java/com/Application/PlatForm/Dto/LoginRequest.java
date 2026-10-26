package com.Application.PlatForm.Dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String userName, @NotBlank String userPassword) {
}