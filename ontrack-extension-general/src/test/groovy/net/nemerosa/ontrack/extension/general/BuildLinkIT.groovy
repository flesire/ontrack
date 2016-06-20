package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.SearchResult
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static org.junit.Assert.assertEquals

class BuildLinkIT extends AbstractServiceTestSupport {

    @Autowired
    private BuildLinkSearchExtension extension

    @Autowired
    @Deprecated
    private PropertyService propertyService

    @Autowired
    private AccountService accountService

    @Test
    void 'Automation role can create links'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asGlobalRole("AUTOMATION").call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        def targets = asUser().withView(build).withView(target).call {
            structureService.getBuildLinksFrom(build)
        }
        assert !targets.empty
        assert targets.find { it.name == target.name }
    }

    @Test(expected = AccessDeniedException)
    void 'Build config is needed on source build to create a link'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asUser().withView(target).call {
            structureService.addBuildLink(build, target)
        }
    }

    @Test(expected = AccessDeniedException)
    void 'Build view is needed on target build to create a link'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildConfig).call {
            structureService.addBuildLink(build, target)
        }
    }

    @Test
    void 'Adding and deleting a build'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildConfig).withView(target).call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        def targets = asUser().withView(build).withView(target).call {
            structureService.getBuildLinksFrom(build)
        }
        assert !targets.empty
        assert targets.find { it.name == target.name }
        // Deleting the build
        asUser().with(build, BuildConfig).withView(target).call {
            structureService.deleteBuildLink(build, target)
        }
        // The build link is deleted
        targets = asUser().withView(build).withView(target).call {
            structureService.getBuildLinksFrom(build)
        }
        assert targets.empty
    }

    @Test
    void 'Adding twice a build'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildConfig).withView(target).call {
            structureService.addBuildLink(build, target)
            // ... twice
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        def targets = asUser().withView(build).withView(target).call {
            structureService.getBuildLinksFrom(build)
        }
        assert !targets.empty
        assert targets.size() == 1
        assert targets.find { it.name == target.name }
    }

    @Test
    void 'Controller role can create links'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asGlobalRole("CONTROLLER").call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        def targets = asUser().withView(build).withView(target).call {
            structureService.getBuildLinksFrom(build)
        }
        assert !targets.empty
        assert targets.find { it.name == target.name }
    }

    @Test(expected = AccessDeniedException)
    void 'Creator role cannot create links'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asGlobalRole("CREATOR").call {
            structureService.addBuildLink(build, target)
        }
    }

    @Test
    void 'Build config function grants access to create links'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildConfig).withView(target).call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        def targets = asUser().withView(build).withView(target).call {
            structureService.getBuildLinksFrom(build)
        }
        assert !targets.empty
        assert targets.find { it.name == target.name }
    }

    @Test
    void 'Build edit function grants access to create links'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildEdit).withView(target).call {
            structureService.addBuildLink(build, target)
        }
        // The build link is created
        def targets = asUser().withView(build).withView(target).call {
            structureService.getBuildLinksFrom(build)
        }
        assert !targets.empty
        assert targets.find { it.name == target.name }
    }

    @Test(expected = AccessDeniedException)
    void 'Build create function does not grant access to create links'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        // Build link creation
        asUser().with(build, BuildCreate).withView(target).call {
            structureService.addBuildLink(build, target)
        }
    }

    @Test
    void 'Searching on build link - found one build'() {
        // Creates a build
        def build = doCreateBuild()
        // Creates a second build to link
        def target = doCreateBuild()
        def targetPrefix = target.name[0..5]
        // Build link on the build
        asUser().with(build, BuildConfig).withView(target).call {
            structureService.addBuildLink(build, target)
        }
        // Searching
        def results = asUser().with(build, ProjectView).call {
            extension.search("${target.project.name}:${targetPrefix}*")
        }
        assertEquals(
                [
                        new SearchResult(
                                build.entityDisplayName,
                                "${build.project.name} -> ${build.name}",
                                URI.create("urn:test:entity:BUILD:${build.id}"),
                                URI.create("urn:test:#:entity:BUILD:${build.id}"),
                                100
                        )
                ],
                results
        )
    }

    /**
     * <pre>
     *     build1 -> target
     *     build2 -> target
     * </pre>
     *
     * Looking for target brings build1 & build2
     */
    @Test
    void 'Searching on build link - found two builds'() {
        // Creates a target build
        def target = doCreateBuild()
        def targetPrefix = target.name[0..5]
        // Two builds to find
        def branch = doCreateBranch()
        def build1 = doCreateBuild(branch, nd("1.1", "Build 1"))
        def build2 = doCreateBuild(branch, nd("1.2", "Build 2"))
        // Meta info on the build
        [build1, build2].each { build ->
            asUser().with(build, BuildConfig).withView(target).call {
                structureService.addBuildLink(build, target)
            }
        }
        // Searching
        def results = asUser().with(target, ProjectView).with(branch, ProjectView).call {
            extension.search("${target.project.name}:${targetPrefix}*")
        }
        assertEquals([
                new SearchResult(
                        build2.entityDisplayName,
                        "${build2.project.name} -> ${build2.name}",
                        URI.create("urn:test:entity:BUILD:${build2.id}"),
                        URI.create("urn:test:#:entity:BUILD:${build2.id}"),
                        100
                ),
                new SearchResult(
                        build1.entityDisplayName,
                        "${build1.project.name} -> ${build1.name}",
                        URI.create("urn:test:entity:BUILD:${build1.id}"),
                        URI.create("urn:test:#:entity:BUILD:${build1.id}"),
                        100
                ),
        ], results)
    }

    @Test
    void 'Searching on build link - found one build among two ones'() {
        // Creates two target builds
        def targetBranch = doCreateBranch()
        def target1 = doCreateBuild(targetBranch, nd('1.0', ''))
        def target2 = doCreateBuild(targetBranch, nd('2.0', ''))
        // Context
        def branch = doCreateBranch()
        def build1 = doCreateBuild(branch, nd("1", "Build 1"))
        def build2 = doCreateBuild(branch, nd("2", "Build 2"))
        // Meta info on the builds
        asUser().with(branch, BuildConfig).withView(targetBranch).call {
            structureService.addBuildLink(build1, target1)
            structureService.addBuildLink(build2, target2)
        }
        // Searching
        def results = asUser().withView(branch).withView(targetBranch).call {
            extension.search("${targetBranch.project.name}:1*")
        }
        assert results == [
                new SearchResult(
                        build1.entityDisplayName,
                        "${build1.project.name} -> 1",
                        URI.create("urn:test:entity:BUILD:${build1.id}"),
                        URI.create("urn:test:#:entity:BUILD:${build1.id}"),
                        100
                ),
        ]
    }

}
