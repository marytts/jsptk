package jsptk;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.io.Resources;

import org.testng.Assert;
import org.testng.annotations.Test;

public class JSPTKWrapperTest {

    public static double[] providerMGC() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.mgc");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);
        double[] mgc = new double[lines.size()];
        for (int i = 0; i < mgc.length; i++) {
            mgc[i] = Double.parseDouble(lines.get(i));
        }

        return mgc;
    }

    public static double[] providerFREQT() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.freqt");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);

        double[] freqt = new double[lines.size()];
        for (int i = 0; i < freqt.length; i++) {
            freqt[i] = Double.parseDouble(lines.get(i));
        }

        return freqt;
    }

    public static double[] providerSP() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.sp");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);

        double[] sp = new double[lines.size()];
        for (int i = 0; i < sp.length; i++) {
            sp[i] = Double.parseDouble(lines.get(i));
        }

        return sp;
    }

    // FIXME: proper provider should be used
    @Test
    public void testFreqt() throws Exception{
        double[] mgc = providerMGC();
        double[] ref = providerFREQT();
        double[] test = JSPTKWrapper.freqt(mgc, 511, 0.55);
        Assert.assertEquals(test.length, ref.length);
        for (int i = 0; i<test.length; i++) {
            Assert.assertEquals(test[i], ref[i], 0.000001);
        }
    }


    // FIXME: proper provider should be used
    @Test
    public void TestFFTR() throws Exception{
        double[] freqt = providerFREQT();
        double[] ref = providerSP();
        double[] test = JSPTKWrapper.fftr(freqt).real;
        Assert.assertEquals(test.length, ref.length);
        for (int i = 0; i<ref.length; i++) {
            Assert.assertEquals(test[i], ref[i], 0.0001);
        }
    }

}
