package jsptk;

// Audio
import javax.sound.sampled.AudioInputStream;

// Assertions
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

// Test annotations
import org.testng.annotations.Test;

public class JSPTKWrapperTest {

    @Test
    public void testMCEP() throws Exception{
        double[] sp = JSPTKProvider.providerSP();
        double[] ref= JSPTKProvider.providerMCEP();
        double[] test = JSPTKWrapper.mcep(sp, 49, 0.55, 2, 30,
                                          0.001, 2, -1e-08, 1e-06, 0);
        assertThat(test).containsExactly(ref, within(1e-4));
    }

    @Test
    public void testMC2B() throws Exception{
        double[] mgc = JSPTKProvider.providerMGC();
        double[] ref = JSPTKProvider.providerB();
        double[] test = JSPTKWrapper.mc2b(mgc, 0.55);
        assertThat(test).containsExactly(ref, within(1e-6));
    }

    @Test
    public void testFreqt() throws Exception{
        double[] mgc = JSPTKProvider.providerMGC();
        double[] ref = JSPTKProvider.providerFREQT();
        double[] test = JSPTKWrapper.freqt(mgc, 511, 0.55);
        assertThat(test).containsExactly(ref, within(1e-6));
    }

    @Test
    public void testC2ACR() throws Exception{
        double[] mgc = JSPTKProvider.providerMGC();
        double[] test = JSPTKWrapper.c2acr(mgc, 0, 512);
        assertThat(test[0]).isCloseTo(0.01000931, within(1e-6));
    }

    @Test
    public void testFFTR() throws Exception{
        double[] freqt = JSPTKProvider.providerFREQT();
        double[] ref = JSPTKProvider.providerSP();
        double[] test = JSPTKWrapper.fftr(freqt).real;
        assertThat(test).containsExactly(ref, within(1e-4));
    }

    @Test
    public void testFrame() throws Exception {
        // Providing data
        double[] x = JSPTKProvider.providerRAWSignal();
        double[][] ref = JSPTKProvider.providerFramedSignal();

        // Run operation
        double[][] test = JSPTKWrapper.frame(x, JSPTKProvider.FRAME_LENGTH, JSPTKProvider.FRAME_SHIFT, false);

        // Assertion
        assertThat(test.length).isEqualTo(ref.length);
        for (int t=0; t<test.length; t++)
            assertThat(test[t]).containsExactly(ref[t], within(1e-4));
    }

    @Test
    public void testWindow() throws Exception {
        // Providing data
        double[][] framed = JSPTKProvider.providerFramedSignal();
        double[][] ref = JSPTKProvider.providerWindowedSignal();

        // Run operation
        double[][] test = JSPTKWrapper.window(framed, JSPTKProvider.FFT_LEN, 1, JSPTKProvider.WIN_TYPE);

        // Assertion
        assertThat(test.length).isEqualTo(ref.length);
        for (int t=0; t<test.length; t++)
            assertThat(test[t]).containsExactly(ref[t], within(1e-4));
    }

    @Test
    public void testMGCEP() throws Exception {
        // Providing data
        double[][] windowed = JSPTKProvider.providerWindowedSignal();
        double[][] ref = JSPTKProvider.providerMGCFromWindowedSignal();

        // Run operation
        double[][] test = JSPTKWrapper.mgcepDefaultWav(windowed, JSPTKProvider.MGC_ORDER,
                                                       JSPTKProvider.ALPHA, JSPTKProvider.PER_ERR);

        // Assertion
        assertThat(test.length).isEqualTo(ref.length);
        for (int t=0; t<test.length; t++)
            assertThat(test[t]).containsExactly(ref[t], within(1e-4));
    }


    @Test
    public void testMGC2SP() throws Exception {
        // Providing data
        double[][] mgc = JSPTKProvider.providerMGCFromWindowedSignal();
        double[][] ref = JSPTKProvider.providerMGC2SP();

        // Run operation
        double[][] test = JSPTKWrapper.mgc2sp(mgc, JSPTKProvider.ALPHA, JSPTKProvider.GAMMA, 0,
                                              false, false, JSPTKProvider.FFT_LEN,
                                              JSPTKProvider.MGC2SP_OTYPE, false);
        // Assertion
        assertThat(test.length).isEqualTo(ref.length);
        for (int t=0; t<test.length; t++)  {
            assertThat(test[t]).containsExactly(ref[t], within(1e-2)); // FIXME: see for precision
            System.out.println("it " + t + " passed");
        }
    }

    @Test
    public void testPITCH() throws Exception {
        // Providing data
        double[] x = JSPTKProvider.providerRAWSignal();
        double[] ref = JSPTKProvider.providerLF0RAPT();

        // Run operation
        double[] test = JSPTKWrapper.pitch(x, JSPTKProvider.FRAME_SHIFT, JSPTKProvider.SAMPLING_RATE,
                                           0, 2, JSPTKProvider.LOWER_F0, JSPTKProvider.UPPER_F0, 0.0);

        // Assertion
        assertThat(test).containsExactly(ref, within(1e-4));
    }

    @Test
    public void testAISToRAW() throws Exception {
        // Providing data
        AudioInputStream ais = JSPTKProvider.providerAIS();
        double[] ref = JSPTKProvider.providerRAWSignal();

        // Run operation
        double[] test = JSPTKWrapper.extractRAWFromStream(ais);

        // Assertion
        assertThat(test).containsExactly(ref, within(1e-4));
    }
}
