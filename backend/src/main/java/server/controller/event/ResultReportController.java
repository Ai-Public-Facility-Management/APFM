package server.event.controller;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import server.repository.ResultReportRepository;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/resultReports")
@Transactional
public class ResultReportController {

    @Autowired
    ResultReportRepository resultReportRepository;
}
//>>> Clean Arch / Inbound Adaptor
