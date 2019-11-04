package st3s0.scaffoldermavenplugin.mojo;

import io.vavr.Tuple;
import io.vavr.collection.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.joox.Match;
import st3s0.scaffoldermavenplugin.Utils;

import java.nio.file.Path;
import java.util.function.BiFunction;

import static java.lang.String.format;
import static org.joox.JOOX.$;
import static st3s0.scaffoldermavenplugin.mojo.AddUsefulSetOfDependencies.MOJO_NAME;

@Mojo(name = MOJO_NAME)
@Setter
@Getter
public class AddUsefulSetOfDependencies extends BaseMojo {

    public static final String MOJO_NAME = "useful-set-of-dependencies";

    @Parameter(defaultValue = "${project}", required = true, readonly = false)
    MavenProject mavenProject;

    // used in tests
    private Path pathToPom;

    @Override
    protected Match modifyPom(Match pom) {
        getLog().info(format("[%s] adding useful set of deps", getMojoName()));
        BiFunction<String, String, String> lastVersionOf = Utils.lastVersionOfFunction();
        List.of(
                Tuple.of("org.projectlombok", "lombok", "compile"),
                Tuple.of("com.google.guava", "guava", "compile"),
                Tuple.of("org.apache.commons", "commons-lang3", "compile"),
                Tuple.of("org.apache.commons", "commons-lang3", "compile"),
                Tuple.of("io.vavr", "vavr", "compile"),
                Tuple.of("com.cedarsoftware", "java-util", "compile"),
                Tuple.of("com.google.truth", "truth", "test")
        ).forEach(t -> addDependencyIfMissing(
                pom, t._1(), t._2(), lastVersionOf.apply(t._1(), t._2()), t._3()
        ));
        return pom;
    }


    private void addDependencyIfMissing(Match pom, String groupId, String artifactId, String version, String scope) {
        final Match dependencies = Utils.findOrCreate(pom, "dependencies");

        if (dependencies.children().each().stream().filter(d -> d.child("artifactId").text().equals(artifactId)).findAny().isEmpty()) {
            getLog().info(format("%s not found, adding it", artifactId));
            dependencies.append(
                    $("dependency").append(
                            $("groupId", groupId),
                            $("artifactId", artifactId),
                            $("version", version),
                            $("scope", scope)
                    )
            );
        } else {
            getLog().info(format("%s already present", artifactId));
        }
    }

    @Override
    public String getMojoName() {
        return MOJO_NAME;
    }
}
