package server.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import server.domain.Camera;
import server.repository.CameraRepository;
import server.service.CameraService;

import java.util.List;



@RestController
@RequestMapping(value="/camera")
@Transactional
public class CameraController {

    @Autowired
    CameraService cameraServie;

    @GetMapping
    @ResponseBody
    public List<Camera> getAllCamera() {
        return cameraServie.getAllCamera();
    }
}

