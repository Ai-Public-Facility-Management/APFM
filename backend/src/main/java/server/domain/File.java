package server.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Data
public class File {
    private String url;
    private String description;

    public File(String url,String description) {
        this.url = url;
        this.description = description;
    }


}