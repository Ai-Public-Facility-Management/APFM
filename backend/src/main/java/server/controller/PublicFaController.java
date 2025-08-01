package server.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import server.domain.PublicFa;
import server.dto.PublicFaDTO;
import server.service.PublicFaService;
import java.util.List;



@RestController
@RequestMapping(value="/api/publicfa")
@Transactional
public class PublicFaController {

    @Autowired
    PublicFaService publicFaService;

    @GetMapping(value="/all")
    @ResponseBody
    public List<PublicFa> viewAllPublicFa() {
        return publicFaService.getFas();
    }

    @GetMapping
    @ResponseBody
    public PublicFa viewPublicFaById(@RequestParam long id) {
       return publicFaService.getFa(id);
    }

    @PostMapping()
    @ResponseBody
    public PublicFa createFa(@RequestBody PublicFaDTO publicFaDTO) {
        return publicFaService.createPublicFa(publicFaDTO);
    }

    @PutMapping
    @ResponseBody
    public PublicFa updateFa(@RequestBody PublicFaDTO publicFaDTO) {
        return publicFaService.updateFa(publicFaDTO);
    }

    @DeleteMapping
    @ResponseBody
    public void deleteFa(@RequestParam Long id) {
        publicFaService.deleteFa(id);
    }

}
//>>> Clean Arch / Inbound Adaptor
