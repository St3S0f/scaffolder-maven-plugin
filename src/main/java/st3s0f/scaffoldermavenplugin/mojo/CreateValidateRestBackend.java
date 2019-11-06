package st3s0f.scaffoldermavenplugin.mojo;

import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.joox.Match;
import st3s0f.scaffoldermavenplugin.JavaParserUtils;
import st3s0f.scaffoldermavenplugin.Utils;

import java.nio.file.Path;

@Mojo(name = CreateValidateRestBackend.MOJO_NAME)
@Setter
@Getter
public class CreateValidateRestBackend extends BaseMojo {

    public static final String MOJO_NAME = "create-validate-rest-backend";
    @Parameter(defaultValue = "${project}", required = true, readonly = false)
    MavenProject mavenProject;

    // used in tests
    private Path pathToPom;

    @Override
    public String getMojoName() {
        return MOJO_NAME;
    }

    @Override
    protected void doStuff(Match pom) throws MojoExecutionException {
        Path rootJavaSourcePath = Utils.getRootJavaSourcePath(pathToPom);

        Boolean hasSpringBootApp = JavaParserUtils.hasAnyAnnotation(rootJavaSourcePath, "SpringBootApplication");
        System.out.println(String.format("hasSpringBootApp: %s", hasSpringBootApp));

        if (!hasSpringBootApp) {
            JavaParserUtils.createSpringBootApp(rootJavaSourcePath);
        }

        JavaParserUtils.createJpaEntity(rootJavaSourcePath, "model.User");
        JavaParserUtils.createJpaEntity(rootJavaSourcePath, "model.Group");

    }

}
