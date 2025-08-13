package server.dto;

import lombok.Data;

@Data
public class CameraResponse {
    private Float longitude;
    private Float latitude;
    private String location;
}
