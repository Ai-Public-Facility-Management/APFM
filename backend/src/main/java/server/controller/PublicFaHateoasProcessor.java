package server.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import server.domain.*;

@Component
public class PublicFaHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<PublicFa>> {

    @Override
    public EntityModel<PublicFa> process(EntityModel<PublicFa> model) {
        return model;
    }
}
