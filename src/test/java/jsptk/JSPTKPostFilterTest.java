package jsptk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.testng.annotations.Test;

public class JSPTKPostFilterTest {

    /**
     *  Baseline postfilter test
     *
     */
    @Test
    public void testPostFilter() throws Exception{
        double[][] mgc = {JSPTKProvider.providerMGC()};
        double[][] ref = {JSPTKProvider.providerPostFilteredMGC()};

        double coef = 1.4;
        int min_phase_order = 511;
        int fftlen = 2048;
        double alpha = 0.55;

        double[][] post_filt = JSPTKSynthesis.postfilter(mgc, min_phase_order, fftlen, coef, alpha);

        for (int t=0; t<ref.length; t++)
            assertThat(post_filt[t]).containsExactly(ref[t], within(1e-6));
    }


    /**
     *  Memory test
     *
     */
    @Test
    public void testPostFilterFullRunning() throws Exception{
        double[][] mgc = JSPTKProvider.providerMGCFull();

        double coef = 1.4;
        int min_phase_order = 511;
        int fftlen = 2048;
        double alpha = 0.55;

        JSPTKSynthesis.postfilter(mgc, min_phase_order, fftlen, coef, alpha);
    }
}
