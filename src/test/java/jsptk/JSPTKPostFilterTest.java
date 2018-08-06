package jsptk;


// EJML
import org.ejml.simple.SimpleMatrix;
import org.ejml.equation.Equation;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.assertj.core.util.DoubleComparator;
import static org.assertj.core.api.Assertions.*;

public class JSPTKPostFilterTest {

    // convert JBLAS DoubleMatrix to Double Array
    public static double[][] matrix2Array(SimpleMatrix m)   {
        int nr = m.numRows();
        int nc = m.numCols();
        double [][] rm = new double[nr][nc];
        for (int r=0; r<nr; r++)
            for (int c=0; c<nc; c++)
                rm[r][c] = m.get(r, c);

        return rm;

    }

    // FIXME: proper provider should be used
    @Test
    public void testPostFilter() throws Exception{
        double[] mgc = JSPTKProvider.providerMGC();
        double[] ref = JSPTKProvider.providerB();

        // FIXME: hardcoded
        double coef = 1.4;
        int min_phase_order = 511;
        int fftlen = 2048;
        double alpha = 0.55;

        // Init MGC matrix
        double[][] c_ar = {mgc}; //, mgc};
        int D = c_ar[0].length;
        SimpleMatrix c = new SimpleMatrix(c_ar);
        System.out.println(c);

        // Init the weights
        SimpleMatrix weights = new SimpleMatrix(c_ar.length, D);
        for (int t=0; t<c_ar.length; t++) {
            weights.set(t, 0, 1.0);
            weights.set(t, 1, 1.0);
            for (int i=2; i<D; i++)
                weights.set(t, i, coef);
        }
        double[][] cw = matrix2Array(c.elementMult(weights));

        // c_r0 = pysptk.c2acr(pysptk.freqt(c, minimum_phase_order, alpha=-alpha), 0, fftlen).flatten()
        SimpleMatrix c_r0 =
            new SimpleMatrix(JSPTKWrapper.c2acr(JSPTKWrapper.freqt(matrix2Array(c),
                                                                   min_phase_order, -alpha),
                                                0, fftlen));

        // c_p_r0 = pysptk.c2acr(pysptk.freqt(c * weight, minimum_phase_order, -alpha), 0, fftlen).flatten()
        SimpleMatrix c_p_r0 =
            new SimpleMatrix(JSPTKWrapper.c2acr(JSPTKWrapper.freqt(cw, min_phase_order, -alpha),
                                                0, fftlen));

        // c_b0 = pysptk.mc2b(weight * c, alpha)[:, 0]
        SimpleMatrix c_b0 = (new SimpleMatrix(JSPTKWrapper.mc2b(cw, alpha))).extractVector(false, 0);

        // c_p_b0 = np.log(c_r0 / c_p_r0) / 2 + c_b0
        SimpleMatrix c_p_b0 = new SimpleMatrix(cw.length, 1);
        Equation eq = new Equation();
        eq.alias(c_p_b0, "c_p_b0", c_r0, "c_r0", c_p_r0, "c_p_r0", c_b0, "c_b0");
        eq.process("c_p_b0 = log(c_r0 ./ c_p_r0) / 2 + c_b0");

        // Fix gain coefficient: c_up = np.hstack((c_p_b0[:, None], pysptk.mc2b(c * weight, alpha)[:, 1:]))
        double[][] c_up = JSPTKWrapper.mc2b(cw, alpha);
        for (int i=0; i<c_up.length; i++)
            c_up[i][0] = c_p_b0.get(i, 0);

        double[][] ref_cw = {{-2.30211123e+00, -1.14071720e-05, -2.89562328e-05,
                              -1.90582677e-05, -5.90205679e-05, -1.94794766e-05,
                              3.21754665e-05,  2.84915882e-05, -2.87736513e-05,
                              7.85758420e-06, -8.89196036e-06,  1.97487570e-05,
                              7.93302360e-06, -4.04950109e-06,  2.37567470e-06,
                              -7.64372267e-05,  1.48828122e-05, -8.85248044e-06,
                              -3.74074829e-06,  5.70539787e-05, -2.45431093e-08,
                              -1.02168103e-04, -2.63792665e-05,  7.67793936e-05,
                              -1.29696611e-05,  2.81375656e-05, -1.62060465e-05,
                              1.72872446e-05, -1.81409757e-05,  4.10793085e-05,
                              3.85919845e-05, -1.11987354e-05, -4.25958993e-05,
                              3.19567139e-06,  7.38776884e-05,  2.59438393e-05,
                              1.00249831e-05,  7.20304532e-06, -3.01838278e-05,
                              -6.64952221e-05,  3.10137856e-05,  2.41433716e-05,
                              2.53321971e-05,  2.69302344e-06, -2.92701772e-05,
                              -4.92220415e-05,  5.88720391e-05, -3.95043257e-05,
                              4.78545340e-05, -4.14094800e-05}};

        assertThat(c_up[0][0]).
                   usingComparator(new DoubleComparator(1e-6)).
                   isEqualTo(ref_cw[0][0]);

        // FIXME: assessed until here !

        // c_p_c = pysptk.b2mc(c_up, alpha)
        double[][] c_p_c = JSPTKWrapper.b2mc(c_up, alpha);

        double[][] post_filt = {{-2.30211751e+00, -2.73331000e-05, -3.94382800e-05,
                                 -5.15195800e-05, -6.97342800e-05, -1.78297000e-06,
                                 4.78458400e-05,  1.26660800e-05, -2.44519800e-05,
                                 2.96700600e-06,  1.96985600e-06,  2.41119200e-05,
                                 5.70579800e-06, -2.74288000e-06, -3.96648000e-05,
                                 -6.82516800e-05,  1.00139480e-05, -1.09098920e-05,
                                 2.76389400e-05,  5.70404800e-05, -5.62170000e-05,
                                 -1.16676700e-04,  1.58494000e-05,  6.96460800e-05,
                                 2.50600000e-06,  1.92242400e-05, -6.69806200e-06,
                                 7.30970800e-06,  4.45264400e-06,  6.23049000e-05,
                                 3.24326800e-05, -3.46264800e-05, -4.08382800e-05,
                                 4.38284000e-05,  8.81468000e-05,  3.14575800e-05,
                                 1.39866580e-05, -9.39806000e-06, -6.67562000e-05,
                                 -4.94376400e-05,  4.42926400e-05,  3.80760800e-05,
                                 2.68133600e-05, -1.34055740e-05, -5.63423000e-05,
                                 -1.68424200e-05,  3.71446600e-05, -1.31843320e-05,
                                 2.50793200e-05, -4.14094800e-05}};
        assertThat(c_p_c).isEqualTo(post_filt);
    }
}
