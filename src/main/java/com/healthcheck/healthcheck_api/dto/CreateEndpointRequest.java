package com.healthcheck.healthcheck_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class CreateEndpointRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "URL is required")
    @URL(
            regexp = "https?://.+",
            message = "URL must be a valid HTTP or HTTPS URL"
    )
    private String url;

    @NotNull(message = "Check interval is required")
    @Min(value = 1, message = "Check interval must be at least 1 minute")
    private Integer checkIntervalMinutes;

    private Boolean active = true;
}