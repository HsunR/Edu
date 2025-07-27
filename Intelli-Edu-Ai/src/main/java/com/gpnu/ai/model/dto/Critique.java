package com.gpnu.ai.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;


public record Critique(
        @JsonProperty(required = true)
        @JsonPropertyDescription("A brief summary of the critique for the generated answer.")
        String summary,

        @JsonProperty(required = true)
        @JsonPropertyDescription("The confidence level in the answer (e.g., 'HIGH', 'MEDIUM', 'LOW').")
        ConfidenceLevel confidence,

        @JsonProperty(required = true)
        @JsonPropertyDescription("Does the answer require more information/retrieval? (true/false)")
        boolean needsMoreInfo,

        @JsonProperty(required = true)
        @JsonPropertyDescription("Suggested improvements or next steps for the answer (e.g., 'Rephrase', 'Add details on X', 'Retrieve more about Y').")
        String suggestedImprovements
) {
    public enum ConfidenceLevel {
        HIGH, MEDIUM, LOW
    }
}