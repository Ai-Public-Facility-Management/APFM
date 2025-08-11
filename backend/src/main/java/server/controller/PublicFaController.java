package server.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.domain.PublicFa;
import server.dto.PublicFaDTO;
import server.service.PublicFaService;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value="/api/publicfa")
@Transactional
public class PublicFaController {

    @Autowired
    PublicFaService publicFaService;

    @GetMapping(value="/all")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewAllPublicFa(@PageableDefault(size = 15) Pageable pageable) {
        Map<String, Object> response = new HashMap<>();
        response.put("publicFas", publicFaService.viewAllFas(pageable));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewPublicFaById(@RequestParam long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("publicFa", publicFaService.viewFa(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewTopPublicFas( @RequestParam(defaultValue = "5") int count) {
        Map<String, Object> response = new HashMap<>();
        response.put("publicFas", publicFaService.viewTopFas(count));

        return ResponseEntity.ok(response);
    }

//    @PostMapping()
//    @ResponseBody
//    public ResponseFa createFa(@RequestBody PublicFaDTO publicFaDTO) {
//        return publicFaService.addPublicFa(publicFaDTO);
//    }

    @PostMapping("/approve")
    public PublicFa approveBox(@RequestParam Long id) {
        return publicFaService.approveFa(id);
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

