package server.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import server.domain.*;

@Component
public class CameraHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Camera>> {

    @Override
    public EntityModel<Camera> process(EntityModel<Camera> model) {
        return model;
    }
}
