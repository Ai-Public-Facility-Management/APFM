package server.controller;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.domain.*;
import server.dto.*;
import server.service.IssueService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public ResponseEntity<Map<String, Object>> viewIssues(@RequestHeader("InspectionId") Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("issues", issueService.getIssues(id));
        return ResponseEntity.ok(response);
    }

}

