package server.controller;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import server.repository.IssueRepository;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/issues")
@Transactional
public class IssueController {

    @Autowired
    IssueRepository issueRepository;
}
//>>> Clean Arch / Inbound Adaptor
