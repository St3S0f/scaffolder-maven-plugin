package st3s0f.scaffoldermavenplugin;

import io.vavr.Tuple;
import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void getPackageAndClassPartsFor() {
        assertEquals(Tuple.of("x.y", "Foo"),Utils.getPackageAndClassPartsFor("x.y.Foo"));
    }
    public void getPackageAndClassPartsFor_2() {
        assertEquals(Tuple.of("", "Foo"),Utils.getPackageAndClassPartsFor("x.y.Foo"));
    }
}
