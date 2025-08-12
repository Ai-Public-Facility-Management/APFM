package server.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class File {
    private String url;
    private String description;

    public File(String url) {
        this.url = url;
    }

    public File() {

    }
}