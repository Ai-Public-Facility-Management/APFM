package server.controller;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import server.domain.*;
import server.dto.*;
import server.service.IssueService;

import java.util.List;


@RestController
@RequestMapping("/api/issue")
@Transactional
public class IssueController {


    @Autowired
    IssueService issueService;

    @PostMapping
    @ResponseBody
    public Issue createIssue(@RequestBody IssueDTO issueDTO) {
        return issueService.createIssue(issueDTO);
    }

    @PutMapping
    @ResponseBody
    public Issue updateIssue(@RequestBody IssueDTO issueDTO) {
        return issueService.updateIssue(issueDTO);
    }

    @PostMapping(value = "/makeproposal")
    @ResponseBody
    public Proposal makeProposal(@RequestBody IssueDTO issueDTO) {
        return issueService.requestProposal(issueDTO.getId());
    }

    @DeleteMapping
    @ResponseBody
    public void deleteIssue(@RequestBody IssueDTO issueDTO) {
        issueService.deleteIssue(issueDTO);
    }

    @GetMapping(value="/all")
    @ResponseBody
    public List<Issue> viewAllIssue() {
        return issueService.getAllIssue();
    }

    @GetMapping
    @ResponseBody
    public Issue viewIssue(@RequestParam Long id) {
        return issueService.getIssue(id);
    }

}

