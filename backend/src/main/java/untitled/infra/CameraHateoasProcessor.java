package untitled.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import untitled.domain.*;

@Component
public class CameraHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Camera>> {

    @Override
    public EntityModel<Camera> process(EntityModel<Camera> model) {
        return model;
    }
}
