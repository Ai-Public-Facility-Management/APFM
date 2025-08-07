package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.domain.Camera;
import server.repository.CameraRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CameraService {

    @Autowired
    CameraRepository cameraRepository;

    public List<Camera> getAllCamera() {
        return  cameraRepository.findAll();
    }
}
