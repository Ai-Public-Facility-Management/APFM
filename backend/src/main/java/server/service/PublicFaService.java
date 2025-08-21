package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import server.domain.*;
import server.dto.DashboardIssue;
import server.dto.InspectionResultDTO;
import server.dto.PublicFaDetail;
import server.dto.PublicFaSummary;
import server.repository.PublicFaRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicFaService {

    @Autowired
    PublicFaRepository publicFaRepository;
    @Autowired
    AzureService azureService;


    // 수정 필요
    @Transactional
    public PublicFa addPublicFa(Camera camera,InspectionResultDTO.Detection detection,String publicFaStatus) throws IOException {
        // 저장된 상태인 객체들만 조회
        List<PublicFa> savedFas = publicFaRepository.findByCameraId(camera.getId());
        PublicFaType type = PublicFaType.valueOf(detection.getPublicFaType().toUpperCase());
        FacilityStatus status = FacilityStatus.valueOf(publicFaStatus);
        Section section = new Section(detection.getBox());
        for (PublicFa savedFa : savedFas) {
            double iou = calculateIoU(section, savedFa.getSection());
            double IOU_THRESHOLD = 0.5;
            if (iou >= IOU_THRESHOLD) {
                if (savedFa.getType() == type) {
                    return savedFa;
                }
                PublicFa fa = publicFaRepository.save(new PublicFa(type,section,status,camera,detection));
                fa.setImage(new File(azureService.azureSaveFile(detection.getCrop_image(),fa.getId(),"facility"),"image"));
                return publicFaRepository.save(fa);
            }
        }
        // 중복 아닌 경우 저장
        PublicFa fa = publicFaRepository.save(new PublicFa(type,section,status,camera,detection));
        fa.setImage(new File(azureService.azureSaveFile(detection.getCrop_image(),fa.getId(),"facility"),"image"));
        return publicFaRepository.save(fa);
    }



    public PublicFaDetail viewFa(Long id){
        PublicFa publicFa = publicFaRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        return new PublicFaDetail(publicFa,azureService.azureBlobSas(publicFa.getImage().getUrl()));
    }

    public List<DashboardIssue>viewTopFas(int count){
        PageRequest pageRequest = PageRequest.of(0, count);
        List<PublicFa> Fas = publicFaRepository.findByStatusOrderByIdDesc(pageRequest,FacilityStatus.ABNORMAL);
        List <DashboardIssue> issues = new ArrayList<>();
        Fas.forEach(fas->{
            DashboardIssue issue = new DashboardIssue();
            issue.setPublicFaId(fas.getId());
            issue.setPublicFaType(fas.getType() != null ? fas.getType().getDisplayName() : null);
            issue.setCameraName(fas.getCamera().getLocation());
            if(fas.getStatus().equals(FacilityStatus.ABNORMAL) && fas.getIssue() != null) {
                issue.setIssueType(fas.getIssue().getType() != null ? fas.getIssue().getType().getDisplayName() : null);
                issue.setIsProcessing(fas.getIssue().isProcessing());
            }
            else
                issue.setIssueType(null);

            issues.add(issue);
        });
        return issues;
    }

    public Page<PublicFaSummary> viewAllFas(Pageable pageable){
        Page<PublicFa> publicFaPage = publicFaRepository.findAllByOrderByIdDesc(pageable);
        return publicFaPage.map(PublicFaSummary::new);
    }


//    @Transactional
//    public PublicFa updateFa(PublicFaDTO publicFaDTO){
//        PublicFa publicFa = publicFaRepository.findById(publicFaDTO.getId()).orElseThrow(
//                () -> new RuntimeException("해당 Id의 시설물이 없습니다.")
//        );
//        return publicFaRepository.save(publicFa.updateFa(publicFaDTO));
//    }

    @Transactional
    public void deleteFa(Long id){
        publicFaRepository.deleteById(id);
    }

    private double calculateIoU(Section boxA, Section boxB) {
        //좌표 배열 [x_center, y_center, width, height]

        double xA1 = boxA.getXCenter() - boxA.getWidth() / 2;
        double yA1 = boxA.getYCenter() - boxA.getHeight()/ 2;
        double xA2 = boxA.getXCenter() + boxA.getWidth() / 2;
        double yA2 = boxA.getYCenter() + boxA.getHeight()/ 2;

        double xB1 = boxB.getXCenter() - boxB.getWidth() / 2;
        double yB1 = boxB.getYCenter() - boxB.getHeight()/ 2;
        double xB2 = boxB.getXCenter() + boxB.getWidth() / 2;
        double yB2 = boxB.getYCenter() + boxB.getHeight()/ 2;

        double interLeft = Math.max(xA1, xB1);
        double interTop = Math.max(yA1, yB1);
        double interRight = Math.min(xA2, xB2);
        double interBottom = Math.min(yA2, yB2);

        double interWidth = interRight - interLeft;
        double interHeight = interBottom - interTop;

        if (interWidth <= 0 || interHeight <= 0) return 0.0;

        double interArea = interWidth * interHeight;
        double boxAArea = (xA2 - xA1) * (yA2 - yA1);
        double boxBArea = (xB2 - xB1) * (yB2 - yB1);

        return interArea / (boxAArea + boxBArea - interArea);
    }

}
