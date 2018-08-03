package jsptk;

import org.testng.Assert;
import org.testng.annotations.Test;

public class JSPTKWrapperTest {


    // FIXME: proper provider should be used
    @Test
    public void testMC2B() throws Exception{
        double[] mgc = JSPTKProvider.providerMGC();
        double[] ref = JSPTKProvider.providerB();
        double[] test = JSPTKWrapper.mc2b(mgc, 0.55);
        Assert.assertEquals(test.length, ref.length);
        for (int i = 0; i<test.length; i++) {
            Assert.assertEquals(test[i], ref[i], 0.00001);
        }
    }

    // FIXME: proper provider should be used
    @Test
    public void testFreqt() throws Exception{
        double[] mgc = JSPTKProvider.providerMGC();
        double[] ref = JSPTKProvider.providerFREQT();
        double[] test = JSPTKWrapper.freqt(mgc, 511, 0.55);
        Assert.assertEquals(test.length, ref.length);
        for (int i = 0; i<test.length; i++) {
            Assert.assertEquals(test[i], ref[i], 0.00001);
        }
    }


    // FIXME: proper provider should be used
    @Test
    public void TestC2ACR() throws Exception{
        double[] mgc = JSPTKProvider.providerMGC();
        double[] test = JSPTKWrapper.c2acr(mgc, 0, 512);
        Assert.assertEquals(test[0], 0.01000931, 0.00001);
    }

    // FIXME: proper provider should be used
    @Test
    public void TestFFTR() throws Exception{
        double[] freqt = JSPTKProvider.providerFREQT();
        double[] ref = JSPTKProvider.providerSP();
        double[] test = JSPTKWrapper.fftr(freqt).real;
        Assert.assertEquals(test.length, ref.length);
        for (int i = 0; i<ref.length; i++) {
            Assert.assertEquals(test[i], ref[i], 0.00001);
        }
    }

}
