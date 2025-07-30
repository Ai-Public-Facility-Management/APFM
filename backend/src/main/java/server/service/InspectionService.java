package server.service;

import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import server.domain.Inspection;
import server.repository.InspectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data
@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository inspectionRepository;

    public Page<Inspection> getPagedInspections(Pageable pageable) {
    return inspectionRepository.findAll(pageable);
}
    public Inspection createInspection(LocalDateTime createDate, Boolean isInspected) {
        Inspection inspection = new Inspection();
        Date converted = Date.from(createDate.atZone(ZoneId.systemDefault()).toInstant());
        inspection.setCreateDate(converted);
        inspection.setIsinspected(isInspected);
        return inspectionRepository.save(inspection);
    }

    public List<Inspection> getAllInspections() {
        return inspectionRepository.findAll();
    }

    public Optional<Inspection> getInspection(Long id) {
        return inspectionRepository.findById(id);
    }

    public Inspection updateInspection(Long id, Boolean isInspected) {
        Inspection inspection = inspectionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 점검이 없습니다."));
        inspection.setIsinspected(isInspected);
        return inspectionRepository.save(inspection);
    }

    public void deleteInspection(Long id) {
        inspectionRepository.deleteById(id);
    }
}
