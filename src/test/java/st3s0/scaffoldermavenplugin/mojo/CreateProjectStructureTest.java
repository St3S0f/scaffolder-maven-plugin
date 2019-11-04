package st3s0.scaffoldermavenplugin.mojo;


import java.nio.file.Path;

public class CreateProjectStructureTest extends MyBaseMojoTestCase {

    public void test() throws Exception {
        Path targetPom = setupFooProjectAndReturnTargetPom();

        CreateProjectStructure mojo = (CreateProjectStructure) lookupMojo(CreateProjectStructure.MOJO_NAME, targetPom.toFile());
        mojo.setPathToPom(targetPom);
        mojo.execute();

    }

}
