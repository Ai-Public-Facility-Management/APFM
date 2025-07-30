package server.controller;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import server.domain.*;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/proposals")
@Transactional
public class ProposalController {

    @Autowired
    ProposalRepository proposalRepository;
}
//>>> Clean Arch / Inbound Adaptor
