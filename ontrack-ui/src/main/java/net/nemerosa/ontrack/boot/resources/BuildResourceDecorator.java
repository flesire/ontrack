package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.*;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.resource.*;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class BuildResourceDecorator extends AbstractResourceDecorator<Build> {

    private final ResourceDecorationContributorService resourceDecorationContributorService;

    public BuildResourceDecorator(ResourceDecorationContributorService resourceDecorationContributorService) {
        super(Build.class);
        this.resourceDecorationContributorService = resourceDecorationContributorService;
    }

    @Override
    public List<Link> links(Build build, ResourceContext resourceContext) {
        LinksBuilder linksBuilder = resourceContext.links()
                .self(on(BuildController.class).getBuild(build.getId()))
                        // Other linked resources
                .link("_lastPromotionRuns", on(PromotionRunController.class).getLastPromotionRuns(build.getId()))
                .link("_promotionRuns", on(PromotionRunController.class).getPromotionRuns(build.getId()))
                .link("_validationRuns", on(ValidationRunController.class).getValidationRuns(build.getId()))
                .link("_validationStampRunViews", on(ValidationRunController.class).getValidationStampRunViews(build.getId()))
                        // Creation of a promoted run
                .link(
                        "_promote",
                        on(PromotionRunController.class).newPromotionRunForm(build.getId()),
                        PromotionRunCreate.class, build.getBranch().getProject().id()
                )
                        // Creation of a validation run
                .link(
                        "_validate",
                        on(ValidationRunController.class).newValidationRunForm(build.getId()),
                        ValidationRunCreate.class,
                        build.getBranch().getProject().id()
                )
                        // Actual properties for this build
                .link("_properties", on(PropertyController.class).getProperties(ProjectEntityType.BUILD, build.getId()))
                        // Actions
                .link("_actions", on(ProjectEntityExtensionController.class).getActions(ProjectEntityType.BUILD, build.getId()))
                        // Extra information
                .link("_extra", on(ProjectEntityExtensionController.class).getInformation(ProjectEntityType.BUILD, build.getId()))
                        // Update link
                .update(on(BuildController.class).updateBuild(build.getId(), null), BuildEdit.class, build.projectId())
                        // Delete link
                .delete(on(BuildController.class).deleteBuild(build.getId()), BuildDelete.class, build.projectId())
                        // Decorations
                .link("_decorations", on(DecorationsController.class).getDecorations(build.getProjectEntityType(), build.getId()))
                        // Events
                .link("_events", on(EventController.class).getEvents(build.getProjectEntityType(), build.getId(), 0, 10))
                        // Signature change
                .link(
                        "_signature",
                        on(BuildController.class).updateBuildSignatureForm(build.getId()),
                        ProjectEdit.class, build
                )
                .page(
                        "_page",
                        "build/%d",
                        build.id()
                );
        // Contributions
        resourceDecorationContributorService.contribute(linksBuilder, build);
        // OK
        return linksBuilder.build();
    }

}
