package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repository.CameraRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CameraService {

    @Autowired
    CameraRepository cameraRepository;

    public List<Map<String, Float>> getAllCamera() {
        List<Map<String, Float>> addresses = new ArrayList<>();
        cameraRepository.findAll().forEach(c -> {
            Map<String, Float> address = new HashMap<>();
            address.put("latitude", c.getLatitude());
            address.put("longitude", c.getLongitude());
            addresses.add(address);
        });
        return addresses;
    }
}
