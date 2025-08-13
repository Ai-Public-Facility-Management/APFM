package server.controller;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import server.repository.ReportRepository;

import java.util.Map;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value="api/upload")
@Transactional
public class ReportController {



    @PostMapping("/result")
    public Map<String,String> uploadReport(@RequestParam("file") MultipartFile file){

        return null;
    }

}
//>>> Clean Arch / Inbound Adaptor
