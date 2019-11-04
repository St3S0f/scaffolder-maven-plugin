package st3s0f.scaffoldermavenplugin.mojo;


import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.IOException;
import java.nio.file.*;

public abstract class MyBaseMojoTestCase extends AbstractMojoTestCase {

    protected Path setupFooProjectAndReturnTargetPom() throws IOException {
        Path originalPom = getTestFile("src/test/resources/project-to-test/pom.xml").toPath();
        Path fooProject = Paths.get("target", "foo-project");
        Path targetPom = Paths.get(fooProject.toString(), "pom.xml");

        createProjectStructure(fooProject);
        Files.copy(originalPom, targetPom, StandardCopyOption.REPLACE_EXISTING);
        return targetPom;
    }

    private void createProjectStructure(Path fooProject) {
        Paths.get(fooProject.toString(), "src/main/java/x/y").toFile().mkdirs();
        Paths.get(fooProject.toString(), "src/test/java/x/y").toFile().mkdirs();
        Paths.get(fooProject.toString(), "src/main/resources").toFile().mkdirs();
        Paths.get(fooProject.toString(), "src/test/resources").toFile().mkdirs();
    }
}
