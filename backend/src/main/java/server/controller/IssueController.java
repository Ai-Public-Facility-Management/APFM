package server.controller;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.domain.Issue;
import server.service.IssueService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/issue")
@Transactional
public class IssueController {


    @Autowired
    IssueService issueService;


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


    @PostMapping("/result")
    @Transactional
    public ResponseEntity<Map<String,String>> uploadReport(@RequestParam("file") MultipartFile file, @RequestParam Long publicFa_id) throws IOException {
        Map<String, String> response = new HashMap<>();
        issueService.uploadResult(file,publicFa_id);
        return ResponseEntity.ok(response);

    }


}

