package jsptk;

import org.ejml.equation.Equation;
import org.ejml.simple.SimpleMatrix;

/**
 *
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class JSPTKSynthesis
{

    protected static double[][] matrix2Array(SimpleMatrix m)   {
        int nr = m.numRows();
        int nc = m.numCols();
        double [][] rm = new double[nr][nc];
        for (int r=0; r<nr; r++)
            for (int c=0; c<nc; c++)
                rm[r][c] = m.get(r, c);

        return rm;
    }


    public static double[][] postfilter(double[][] c_ar, int min_phase_order, int fftlen, double coef, double alpha)
    {

        // Init MGC matrix
        int D = c_ar[0].length;
        SimpleMatrix c = new SimpleMatrix(c_ar);

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
        SimpleMatrix c_b0 = (new SimpleMatrix(JSPTKWrapper.mc2b(cw, alpha))); //.extractVector(false, 0);
        c_b0 = c_b0.extractVector(false, 0);

        // c_p_b0 = np.log(c_r0 / c_p_r0) / 2 + c_b0
        SimpleMatrix c_p_b0 = new SimpleMatrix(cw.length, 1);
        Equation eq = new Equation();
        eq.alias(c_p_b0, "c_p_b0", c_r0, "c_r0", c_p_r0, "c_p_r0", c_b0, "c_b0");
        eq.process("c_p_b0 = log(c_r0 ./ c_p_r0) / 2 + c_b0");

        // Fix gain coefficient: c_up = np.hstack((c_p_b0[:, None], pysptk.mc2b(c * weight, alpha)[:, 1:]))
        double[][] c_up = JSPTKWrapper.mc2b(cw, alpha);
        for (int i=0; i<c_up.length; i++)
            c_up[i][0] = c_p_b0.get(i, 0);

        // c_p_c = pysptk.b2mc(c_up, alpha)
        c_up = JSPTKWrapper.b2mc(c_up, alpha);

        return c_up;
    }
}
