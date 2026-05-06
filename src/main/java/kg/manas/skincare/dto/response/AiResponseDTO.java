package kg.manas.skincare.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AiResponseDTO {
    @JsonProperty("acne_severity")
    private String acneSeverity; // MILD, MODERATE, SEVERE

    private Double confidence;
    private Integer count;
    private Double score;
}