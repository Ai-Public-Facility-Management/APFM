package untitled.infra;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.NameParser;
import javax.naming.NameParser;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import untitled.config.kafka.KafkaProcessor;
import untitled.domain.*;

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
