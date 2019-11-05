package st3s0f.scaffoldermavenplugin.mojo;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.joox.Match;
import st3s0f.scaffoldermavenplugin.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;
import static org.joox.JOOX.$;
import static st3s0f.scaffoldermavenplugin.Utils.toPrettyString;

public abstract class BaseMojo extends AbstractMojo {

    public abstract String getMojoName();

    public abstract MavenProject getMavenProject();
    public abstract void setMavenProject(MavenProject project);

    public abstract Path getPathToPom();
    public abstract void setPathToPom(Path pathToPom);

    /**
     * Mojos that modify pom.xml should override this
     */
    protected Match modifyPom(Match pom) {return pom;}
    /**
     * Mojos generic actions
     */
    protected void doStuff(Match pom) throws MojoExecutionException {}

    @Override
    public final void execute() throws MojoExecutionException {
        setPathToPom(Utils.getPathToPom(getPathToPom(), getMavenProject()));
        Match pom = readPom();
        getLog().info(format("[%s] modifying pom", getMojoName()));
        writePom(modifyPom(pom), getPathToPom());
        pom = readPom();
        getLog().info(format("[%s] doing stuff", getMojoName()));
        doStuff(pom);

    }

    private void writePom(Match pom, Path pathToPom) throws MojoExecutionException {
        try {
            getLog().info(format("[%s] writing pom.xml", getMojoName()));
            Files.write(pathToPom, toPrettyString(pom.toString(), 2).getBytes());
        } catch (IOException e) {
            throw new MojoExecutionException("Error writing pom.xml", e);
        }
    }

    private Match readPom() throws MojoExecutionException {
        try {
            getLog().info(format("[%s] reading pom.xml", getMojoName()));
            Match pom = $(getPathToPom().toFile());
            return pom;
        } catch (Exception e) {
            throw new MojoExecutionException("Error reading pom.xml", e);
        }
    }
}
