package st3s0f.scaffoldermavenplugin.mojo;


import org.junit.Ignore;

import java.nio.file.Path;

@Ignore
public class CreateValidateRestBackendTest extends MyBaseMojoTestCase {

    public void test() throws Exception {
        Path targetPom = setupFooProjectAndReturnTargetPom();

        CreateValidateRestBackend mojo = (CreateValidateRestBackend) lookupMojo(CreateValidateRestBackend.MOJO_NAME, targetPom.toFile());
        mojo.setPathToPom(targetPom);
        mojo.execute();
    }

}
