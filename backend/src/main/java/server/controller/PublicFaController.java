package server.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import server.repository.PublicFaRepository;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/publicFas")
@Transactional
public class PublicFaController {

    @Autowired
    PublicFaRepository publicFaRepository;
}
//>>> Clean Arch / Inbound Adaptor
