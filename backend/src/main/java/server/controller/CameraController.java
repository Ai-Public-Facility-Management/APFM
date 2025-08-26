package server.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import server.service.CameraService;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value="/camera")
@Transactional
public class CameraController {

    @Autowired
    CameraService cameraServie;

    @GetMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllCamera() {
        Map<String, Object> response = new HashMap<>();
        response.put("cameras", cameraServie.getAllCamera());
        return ResponseEntity.ok(response);
    }
}

