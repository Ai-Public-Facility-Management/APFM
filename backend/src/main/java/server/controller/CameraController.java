package server.controller;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import server.repository.CameraRepository;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/cameras")
@Transactional
public class CameraController {

    @Autowired
    CameraRepository cameraRepository;
}
//>>> Clean Arch / Inbound Adaptor
