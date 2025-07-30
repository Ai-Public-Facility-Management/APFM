package server.controller;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import server.repository.ReportRepository;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/reports")
@Transactional
public class ReportController {

    @Autowired
    ReportRepository reportRepository;
}
//>>> Clean Arch / Inbound Adaptor
