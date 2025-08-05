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
import server.domain.Camera;
import server.domain.FacilityStatus;
import server.domain.PublicFa;
import server.domain.Section;
import server.dto.*;
import server.repository.CameraRepository;
import server.repository.PublicFaRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicFaService {

    @Autowired
    PublicFaRepository publicFaRepository;
    @Autowired
    CameraRepository  cameraRepository;


    // 수정 필요
    @Transactional
    public ResponseFa addPublicFa(PublicFaDTO publicFaDTO) {
        // 저장된 상태인 객체들만 조회
        List<PublicFa> savedFas = publicFaRepository.findByStatusAndCameraId(FacilityStatus.NORMAL,publicFaDTO.getCameraId());
        Camera camera = cameraRepository.findById(publicFaDTO.getCameraId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        for (PublicFa savedFa : savedFas) {
            double iou = calculateIoU(publicFaDTO.getSection(), savedFa.getSection());
            double IOU_THRESHOLD = 0.5;
            if (iou >= IOU_THRESHOLD) {
                if (savedFa.getType() == publicFaDTO.getType()) {
                    return new ResponseFa("이미 존재하는 객체입니다.",new PublicFa());
                }
                else {
                    // IoU 임계값 이상 + 클래스 다름 → 사용자 판단 필요 대기
                    PublicFa publicFa = new PublicFa(publicFaDTO,camera);
                    publicFaRepository.save(publicFa);
                    return new ResponseFa("중복 판단이 모호한 객체가 있습니다. 승인 필요합니다.",publicFaRepository.save(publicFa));
                }
            }
        }
        // 중복 아닌 경우 저장
        PublicFa publicFa = new PublicFa(publicFaDTO,camera);
        return new ResponseFa("새로운 객체로 등록되었습니다.",publicFaRepository.save(publicFa));
    }


    public PublicFaDetail viewFa(Long id){
        PublicFa publicFa = publicFaRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new PublicFaDetail(publicFa);
    }

    public List<DashboardIssue>viewTopFas(int count){
        PageRequest pageRequest = PageRequest.of(0, count);
        List<PublicFa> Fas = publicFaRepository.findByStatusOrderByIdDesc(pageRequest,FacilityStatus.ABNORMAL);
        List <DashboardIssue> issues = new ArrayList<>();
        Fas.forEach(fas->{
            DashboardIssue issue = new DashboardIssue();
            issue.setPublicFaId(fas.getId());
            issue.setIssueType(fas.getIssue().getType());
            issue.setPublicFaType(fas.getType());
            //이슈사항에 제안서가 작성되어 있으면 공사중 표시
            if(fas.getIssue().getProposal() != null)
                issue.setIsProcessing(Boolean.TRUE);
            else
                issue.setIsProcessing(Boolean.FALSE);
            issues.add(issue);
        });
        return issues;
    }

    public Page<PublicFaSummary> viewAllFas(Pageable pageable){
        Page<PublicFa> publicFaPage = publicFaRepository.findAllByOrderByIdDesc(pageable);
        return publicFaPage.map(PublicFaSummary::new);
    }

    // 수정 필요
    public PublicFa approveFa(Long id){
        PublicFa publicFa = publicFaRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        publicFa.setStatus(FacilityStatus.NORMAL);
        return publicFaRepository.save(publicFa);
    }

    @Transactional
    public PublicFa updateFa(PublicFaDTO publicFaDTO){
        PublicFa publicFa = publicFaRepository.findById(publicFaDTO.getId()).orElseThrow(
                () -> new RuntimeException("해당 Id의 시설물이 없습니다.")
        );
        return publicFaRepository.save(publicFa.updateFa(publicFaDTO));
    }

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
