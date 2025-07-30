package server.domain;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import java.util.Date;

@Service
public class InspectionService {

    @Autowired
    private InspectionRepository inspectionRepository;

    public Inspection createInspection(Date createDate, boolean isInspected) {
        Inspection inspection = new Inspection();
        inspection.setCreateDate(createDate);
        inspection.setIsinspected(isInspected);
        return inspectionRepository.save(inspection);
    }

    public Optional<Inspection> getInspection(Long id) {
        return inspectionRepository.findById(id);
    }

    public Iterable<Inspection> getAllInspections() {
        return inspectionRepository.findAll();
    }

    public void deleteInspection(Long id) {
        inspectionRepository.deleteById(id);
    }

    public Inspection updateInspection(Long id, boolean isInspected) {
        Inspection inspection = inspectionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Inspection not found"));
        inspection.setIsinspected(isInspected);
        return inspectionRepository.save(inspection);
    }
}
