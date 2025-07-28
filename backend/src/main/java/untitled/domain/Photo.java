package untitled.domain;

import javax.persistence.*;
import lombok.Data;

@Embeddable
@Data
public class Photo {
    private String url;
    private String description;
}