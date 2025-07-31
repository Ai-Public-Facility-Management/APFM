package server.controller;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import server.domain.*;
import server.dto.*;
import server.service.IssueService;

import java.util.List;


@RestController
@RequestMapping("/api/issues")
@Transactional
public class IssueController {


    @Autowired
    IssueService issueService;

    @PostMapping
    public Issue createIssue(@RequestBody IssueDTO issueDTO) {
        return issueService.createIssue(issueDTO);
    }

    @PostMapping(value = "/makeproposal")
    public Proposal makeProposal(@RequestBody IssueDTO issueDTO) {
        return issueService.requestProposal(issueDTO.getId());
    }

    @DeleteMapping
    public void deleteIssue(@RequestBody IssueDTO issueDTO) {
        issueService.deleteIssue(issueDTO);
    }

    @GetMapping
    public List<Issue> getAllIssue() {
        return issueService.getAllIssue();
    }

}

