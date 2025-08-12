package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.dto.CameraResponse;
import server.repository.CameraRepository;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CameraService {

    @Autowired
    CameraRepository cameraRepository;

    public List<CameraResponse> getAllCamera() {
        List<CameraResponse> responses = new ArrayList<>();
        cameraRepository.findAll().forEach(c -> {
            CameraResponse cameraResponse = new CameraResponse();
            cameraResponse.setLatitude(c.getLatitude());
            cameraResponse.setLongitude(c.getLongitude());
            cameraResponse.setLocation(c.getLocation());
            responses.add(cameraResponse);
        });
        return responses;
    }
}
