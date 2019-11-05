package st3s0f.scaffoldermavenplugin.mojo;

import io.vavr.Tuple;
import io.vavr.collection.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.joox.Match;
import st3s0f.scaffoldermavenplugin.Utils;

import java.nio.file.Path;
import java.util.function.BiFunction;

import static java.lang.String.format;
import static org.joox.JOOX.$;
import static st3s0f.scaffoldermavenplugin.mojo.AddUsefulSetOfDependencies.MOJO_NAME;

@Mojo(name = MOJO_NAME)
@Setter
@Getter
public class AddUsefulSetOfDependencies extends BaseMojo {

    public static final String MOJO_NAME = "useful-set-of-dependencies";



    @Parameter(defaultValue = "${project}", required = true, readonly = false)
    MavenProject mavenProject;

    // used in tests
    private Path pathToPom;

    private final BiFunction<String, String, String> lastVersionOfFunction;

    public AddUsefulSetOfDependencies() {
        lastVersionOfFunction = Utils.lastVersionOfFunction();
    }

    @Override
    protected Match modifyPom(Match pom) {
        getLog().info(format("[%s] adding useful set of deps", getMojoName()));
        List.of(
                Tuple.of("org.projectlombok", "lombok", "compile"),
                Tuple.of("com.google.guava", "guava", "compile"),
                Tuple.of("org.apache.commons", "commons-lang3", "compile"),
                Tuple.of("org.apache.commons", "commons-lang3", "compile"),
                Tuple.of("io.vavr", "vavr", "compile"),
                Tuple.of("com.cedarsoftware", "java-util", "compile"),
                Tuple.of("com.google.truth", "truth", "test")
        ).forEach(t -> addDependencyIfMissing(
                pom, t._1(), t._2(), lastVersionOfFunction.apply(t._1(), t._2()), t._3()
        ));

        Match dm = Utils.findOrCreate(pom, "dependencyManagement");
        Match dmDeps = Utils.findOrCreate(dm, "dependencies");
        if (hasNotChildWithArtifactIdMatching(dmDeps, "spring-boot-dependencies")) {
            appendLatestVersionOfDependency(dmDeps,"org.springframework.boot","spring-boot-dependencies", "import", "pom");
        }


        return pom;
    }

    private boolean hasNotChildWithArtifactIdMatching(Match parent, String artifactId) {
        return parent.children().each().stream().filter(d -> d.child("artifactId").text().equals(artifactId)).findAny().isEmpty();
    }

    private Match appendDependency(Match parent, String g, String a, String v, String scope, String type) {
        Match dep = $("dependency").append(
                $("groupId", g),
                $("artifactId", a),
                $("version", v),
                $("scope", scope)
        );

        return parent.append(
                type != null
                 ? dep.append($("type", type))
                 : dep
        );
    }

    private Match appendLatestVersionOfDependency(Match parent, String g, String a, String scope, String type) {
        return appendDependency(parent, g, a, lastVersionOfFunction.apply(g,a),scope, type);
    }

    private void addDependencyIfMissing(Match pom, String groupId, String artifactId, String version, String scope) {
        final Match dependencies = Utils.findOrCreate(pom, "dependencies");

        if (hasNotChildWithArtifactIdMatching(dependencies, artifactId)) {
            getLog().info(format("%s not found, adding it", artifactId));
            appendDependency(dependencies, groupId,artifactId,version,scope,null);
        } else {
            getLog().info(format("%s already present", artifactId));
        }
    }

    @Override
    public String getMojoName() {
        return MOJO_NAME;
    }
}
