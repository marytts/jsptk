package jsptk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.testng.annotations.Test;

public class JSPTKConversionTest {

    @Test
    public void testMC2SP() throws Exception {
        double[] mgc = JSPTKProvider.providerMGC();
        double[] ref = JSPTKProvider.providerMC2SP();
        double[] test = JSPTKConversion.mc2sp(mgc, 0.55, 512);

        assertThat(test).containsExactly(ref, within(1e-6));
    }
}
