package jsptk;

import org.testng.Assert;
import org.testng.annotations.Test;

public class JSPTKConversionTest {

    @Test
    public void testMC2SP() throws Exception {
        double[] mgc = JSPTKProvider.providerMGC();
        double[] ref = JSPTKProvider.providerMC2SP();

        double[] test = JSPTKConversion.mc2sp(mgc, 0.55, 512);

        Assert.assertEquals(test.length, ref.length);
        for (int i = 0; i<test.length; i++) {
            Assert.assertEquals(test[i], ref[i], 0.000001);
        }
    }
}
