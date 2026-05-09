package kg.manas.skincare.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AiResponseDTO {
    @JsonProperty("acne_severity")
    private String acneSeverity; // MILD, MODERATE, SEVERE

    private Double confidence;
    private Integer count;
    private Double score;

    @JsonProperty("image_width")
    private Integer imageWidth;

    @JsonProperty("image_height")
    private Integer imageHeight;

    private List<AiBoxDTO> boxes;
}
