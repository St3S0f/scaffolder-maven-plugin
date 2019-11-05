package st3s0f.scaffoldermavenplugin.mojo;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.joox.Match;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        CompilationUnit cu = new CompilationUnit();

        ClassOrInterfaceDeclaration user = cu
                .setPackageDeclaration("x.y")
                .addClass("User");

        user
                .addAnnotation(lombok.Data.class)
                .addField("String", "name");


        try {
            FileUtils.write(
                    Paths.get(
                            pathToPom.getParent().toString(),
                            "src/main/java",
                            "x/y/User.java"
                    ).toFile(),
                    cu.toString()
            );
        } catch (IOException e) {
            throw new MojoExecutionException(e.toString());
        }
    }
}
