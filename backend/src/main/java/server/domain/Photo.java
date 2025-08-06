package server.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Photo {
    private String url;
    private String description;
}