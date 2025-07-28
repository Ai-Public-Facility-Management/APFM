package untitled.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import untitled.domain.*;

@Component
public class ProposalHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Proposal>> {

    @Override
    public EntityModel<Proposal> process(EntityModel<Proposal> model) {
        return model;
    }
}
