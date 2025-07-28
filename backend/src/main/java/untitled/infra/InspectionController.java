package untitled.infra;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import untitled.domain.*;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/inspections")
@Transactional
public class InspectionController {

    @Autowired
    InspectionService inspectionService;

    @PostMapping("/inspections")
    public Inspection create(@RequestBody Inspection inspection) {
        return inspectionService.createInspection(
            inspection.getCreateDate(), inspection.getIsinspected()
        );
    }

    @GetMapping("/inspections")
    public Iterable<Inspection> getAll() {
        return inspectionService.getAllInspections();
    }

    @GetMapping("/inspections/{id}")
    public Optional<Inspection> getOne(@PathVariable("id") Long id) {
        return inspectionService.getInspection(id);
    }

    @PutMapping("/inspections/{id}")
    public Inspection update(@PathVariable("id") Long id, @RequestBody Inspection updated) {
        return inspectionService.updateInspection(id, updated.getIsinspected());
    }

    @DeleteMapping("/inspections/{id}")
    public void delete(@PathVariable("id") Long id) {
        inspectionService.deleteInspection(id);
    }
}
//>>> Clean Arch / Inbound Adaptor
