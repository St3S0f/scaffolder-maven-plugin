package st3s0.scaffoldermavenplugin.mojo;


import java.nio.file.Path;

public class AddUsefulSetOfDependenciesTest extends MyBaseMojoTestCase {

    public void test() throws Exception {
        Path targetPom = setupFooProjectAndReturnTargetPom();

        AddUsefulSetOfDependencies mojo = (AddUsefulSetOfDependencies) lookupMojo(AddUsefulSetOfDependencies.MOJO_NAME, targetPom.toFile());
        mojo.setPathToPom(targetPom);
        mojo.execute();
    }

}
