package server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import server.domain.PublicFa;

@Data
@AllArgsConstructor
public class ResponseFa {
    private String message;
    private PublicFa publicFa;
}
