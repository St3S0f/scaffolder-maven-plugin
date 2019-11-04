package st3s0.scaffoldermavenplugin.mojo;

import com.google.common.base.Splitter;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.joox.Match;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.lang.String.format;
import static st3s0.scaffoldermavenplugin.mojo.CreateProjectStructure.MOJO_NAME;

@Mojo( name = MOJO_NAME)
@Setter
@Getter
public class CreateProjectStructure extends BaseMojo {

    public static final String MOJO_NAME = "create-project-structure";

    @Parameter(defaultValue = "${project}", required = true, readonly = false)
    MavenProject mavenProject;

    // used in tests
    private Path pathToPom;

    @Override
    protected void doStuff(Match pom) {
        getLog().info(format("[%s] creating prj structure", getMojoName()));
        String groupId = pom.child("groupId").text();
        String artifactId = pom.child("artifactId").text();

        ArrayList<String> pathTokens = new ArrayList<>(Splitter.on(".").splitToList(groupId));
        pathTokens.add(artifactId.replace("-",""));
        pathTokens.add(0, "src/main/java");
        Paths.get(pathToPom.getParent().toString(), pathTokens.toArray(new String[pathTokens.size()])).toFile().mkdirs();
        pathTokens.set(0, "src/test/main");
        Paths.get(pathToPom.getParent().toString(), pathTokens.toArray(new String[pathTokens.size()])).toFile().mkdirs();
        Paths.get(pathToPom.getParent().toString(), "src/main/resources").toFile().mkdirs();
        Paths.get(pathToPom.getParent().toString(), "src/test/resources").toFile().mkdirs();
    }

    @Override
    public String getMojoName() {
        return MOJO_NAME;
    }
}
