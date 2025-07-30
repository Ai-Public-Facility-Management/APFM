package server.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import server.domain.*;

@Component
public class IssueHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Issue>> {

    @Override
    public EntityModel<Issue> process(EntityModel<Issue> model) {
        return model;
    }
}
