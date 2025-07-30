package server.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import server.domain.*;

@Component
public class UsersHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Users>> {

    @Override
    public EntityModel<Users> process(EntityModel<Users> model) {
        return model;
    }
}
