package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.domain.Camera;
import server.domain.PublicFa;
import server.dto.PublicFaDTO;
import server.repository.CameraRepository;
import server.repository.PublicFaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicFaService {

    @Autowired
    PublicFaRepository publicFaRepository;
    @Autowired
    CameraRepository  cameraRepository;

    public PublicFa createPublicFa(PublicFaDTO publicFaDTO) {
        PublicFa publicFa = new PublicFa(publicFaDTO);
        publicFa.setCamera(cameraRepository.findById(publicFaDTO.getCameraId()).orElse(new Camera()));
        return publicFaRepository.save(publicFa);
    }

    public List<PublicFa> getFas(){
        return publicFaRepository.findAll();
    }

    public PublicFa getFa(Long id){
        return publicFaRepository.findById(id).orElse(null);
    }

    public PublicFa updateFa(PublicFaDTO publicFaDTO){
        PublicFa publicFa = publicFaRepository.findById(publicFaDTO.getId()).orElseThrow(
                () -> new RuntimeException("해당 Id의 시설물이 없습니다.")
        );
        return publicFaRepository.save(publicFa.updateFa(publicFaDTO));
    }

    public void deleteFa(Long id){
        publicFaRepository.deleteById(id);
    }

}
