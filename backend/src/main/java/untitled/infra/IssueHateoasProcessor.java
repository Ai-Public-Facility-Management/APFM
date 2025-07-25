package untitled.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import untitled.domain.*;

@Component
public class IssueHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Issue>> {

    @Override
    public EntityModel<Issue> process(EntityModel<Issue> model) {
        return model;
    }
}
