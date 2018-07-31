package jsptk;

import org.testng.Assert;
import org.testng.annotations.Test;

public class JSPTKUtilTest {

    private static final int N = 10;
    private static final int samplerate = 48000;

    @Test
    public void testWarpingVector() {
        // Reference coming from pysptk
        double[] ref = {0., 0.29219939, 0.50895349, 0.65312174, 0.75139362, 0.82279594, 0.87822527, 0.92392179, 0.96367697, 1.};
        double[] test = JSPTKUtil.generateWarpingVector(0.5, N);
        for (int i = 0; i<N; i++) {
            Assert.assertEquals(ref[i], test[i], 0.000001);
        }
    }


    @Test
    public void testMelScaleVector() {
        // Reference coming from pysptk
        double[] ref = {0., 0.39249362, 0.56378645, 0.6748454 , 0.7571815 , 0.82263969, 0.87697609, 0.92342678, 0.96399323, 1.};
        double[] test = JSPTKUtil.generateMelScaleVector(samplerate, N);
        for (int i = 0; i<N; i++) {
            Assert.assertEquals(ref[i], test[i], 0.000001);
        }
    }

    @Test
    public void testRMS() {
        // all values are computed first using pysptk
        double[] ref_warp = {0., 0.29219939, 0.50895349, 0.65312174, 0.75139362, 0.82279594, 0.87822527, 0.92392179, 0.96367697, 1.};
        double[] ref_mel = {0., 0.39249362, 0.56378645, 0.6748454 , 0.7571815 , 0.82263969, 0.87697609, 0.92342678, 0.96399323, 1.};

        Assert.assertEquals(JSPTKUtil.computeRMS(ref_mel, ref_warp), .0013572932530366507, 0.000001);
    }

    @Test
    public void computeAlpha() {
        double alpha_c = JSPTKUtil.getMCEPAlpha(samplerate);
        Assert.assertEquals(alpha_c, 0.554, 0.000001);
    }
}
