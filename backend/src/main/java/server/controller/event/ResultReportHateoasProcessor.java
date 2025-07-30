package server.controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import server.domain.*;

@Component
public class ResultReportHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<ResultReport>> {

    @Override
    public EntityModel<ResultReport> process(EntityModel<ResultReport> model) {
        return model;
    }
}
