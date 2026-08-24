package com.healthcheck.healthcheck_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class UpdateEndpointRequest {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @URL(
            regexp = "https?://.+",
            message = "URL must be a valid HTTP or HTTPS URL"
    )
    private String url;

    @Min(value = 1, message = "Check interval must be at least 1 minute")
    private Integer checkIntervalMinutes;

    private Boolean active;
}