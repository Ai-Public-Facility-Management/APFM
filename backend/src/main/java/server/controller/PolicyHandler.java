package server.controller;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import server.config.kafka.KafkaProcessor;
import server.domain.*;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    CameraRepository cameraRepository;

    @Autowired
    InspectionRepository inspectionRepository;

    @Autowired
    PublicFaRepository publicFaRepository;

    @Autowired
    ProposalRepository proposalRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ResultReportRepository resultReportRepository;

    @Autowired
    IssueRepository issueRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}
}
//>>> Clean Arch / Inbound Adaptor
