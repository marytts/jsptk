package jsptk;

import org.testng.Assert;
import org.testng.annotations.Test;

public class JSPTKConversionTest {

    private static final int N = 50;
    private static final int samplerate = 48000;

    @Test
    public void testWarpingVector() {
        double[] mgc = {-2.30212, -2.73331e-05, -2.81702e-05, -3.67997e-05, -4.98102e-05, -1.27355e-06, 3.41756e-05, 9.0472e-06, -1.74657e-05, 2.11929e-06, 1.40704e-06, 1.72228e-05, 4.07557e-06, -1.9592e-06, -2.8332e-05, -4.87512e-05, 7.15282e-06, -7.79278e-06, 1.97421e-05, 4.07432e-05, -4.0155e-05, -8.33405e-05, 1.1321e-05, 4.97472e-05, 1.79e-06, 1.37316e-05, -4.78433e-06, 5.22122e-06, 3.18046e-06, 4.45035e-05, 2.31662e-05, -2.47332e-05, -2.91702e-05, 3.1306e-05, 6.2962e-05, 2.24697e-05, 9.99047e-06, -6.7129e-06, -4.7683e-05, -3.53126e-05, 3.16376e-05, 2.71972e-05, 1.91524e-05, -9.57541e-06, -4.02445e-05, -1.20303e-05, 2.65319e-05, -9.41738e-06, 1.79138e-05, -2.95782e-05};

        double[] sp_test = JSPTKConversion.mc2sp(mgc, 0.55, 2048);
        for (int i = 0; i<N; i++) {
            System.out.println(sp_test[i]);
            // Assert.assertEquals(ref[i], test[i], 0.000001);
        }
        Assert.assertEquals(0, 1, 0.0001);
    }

}
