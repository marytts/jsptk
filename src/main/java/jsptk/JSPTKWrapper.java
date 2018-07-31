package jsptk;

import jsptk.Sptk;
/**
 *
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class JSPTKWrapper
{
    public JSPTKWrapper()
    {
    }


    public double[] c2acr(double[] c, int order, int fftlen) {
        SWIGTYPE_p_double c_sp = JSPTKWrapper.java2swig(c);
        SWIGTYPE_p_double r_sp = Sptk.new_double_array(order);

        // Convert c to
        Sptk.c2acr(c_sp, c.length, r_sp, order, fftlen);

        double[] r = JSPTKWrapper.swig2java(r_sp, order);
        Sptk.delete_double_array(c_sp);
        Sptk.delete_double_array(r_sp);

        return r;

    }

    public double[] freqt(double[] c, int order, double alpha) {
        SWIGTYPE_p_double c_sp = JSPTKWrapper.java2swig(c);
        SWIGTYPE_p_double c2_sp = Sptk.new_double_array(order);

        // Convert c to
        Sptk.freqt(c_sp, c.length, c2_sp, order, alpha);

        double[] c2 = JSPTKWrapper.swig2java(c_sp, order);
        Sptk.delete_double_array(c_sp);
        Sptk.delete_double_array(c2_sp);

        return c2;
    }



    public double[] mc2b(double[] mc, double alpha) {
        SWIGTYPE_p_double mc_sp = JSPTKWrapper.java2swig(mc);
        SWIGTYPE_p_double b_sp = Sptk.new_double_array(mc.length);

        // Convert c to
        Sptk.mc2b(mc_sp, b_sp, mc.length, alpha);
        double[] b = JSPTKWrapper.swig2java(b_sp, mc.length);

        Sptk.delete_double_array(mc_sp);
        Sptk.delete_double_array(b_sp);

        return b;
    }


    public double[] b2mc(double[] b, double alpha) {
        SWIGTYPE_p_double b_sp = JSPTKWrapper.java2swig(b);
        SWIGTYPE_p_double mc_sp = Sptk.new_double_array(b.length);

        // Convert c to
        Sptk.b2mc(b_sp, mc_sp, b.length, alpha);
        double[] mc = JSPTKWrapper.swig2java(mc_sp, b.length);
        Sptk.delete_double_array(mc_sp);
        Sptk.delete_double_array(b_sp);

        return mc;
    }


    private static double[][] swig2java(SWIGTYPE_p_p_double ar, int nb_rows, int nb_cols) {
        double[][] res = new double[nb_rows][nb_cols];

        for (int i=0; i<nb_rows; i++) {
            for (int j=0; j<nb_cols; j++) {
                res[i][j] = Sptk.double_array_getitem(Sptk.double_p_array_getitem(ar, i), j);
            }
        }

        return res;
    }

    private static double[] swig2java(SWIGTYPE_p_double ar, int length) {
        double[] res = new double[length];

        for (int i=0; i<length; i++)
            res[i] = Sptk.double_array_getitem(ar, i);

        return res;
    }


    private static SWIGTYPE_p_double java2swig(double[] ar) {
        SWIGTYPE_p_double res = Sptk.new_double_array(ar.length);

        for (int i=0; i<ar.length; i++)
            Sptk.double_array_setitem(res, i, ar[i]);

        return res;
    }


    private static SWIGTYPE_p_p_double java2swig(double[][] ar) {
        SWIGTYPE_p_p_double res = Sptk.new_double_p_array(ar.length);

        for (int i=0; i<ar.length; i++) {
            SWIGTYPE_p_double tmp = Sptk.new_double_array(ar[i].length);
            for (int j=0; j<ar[j].length; j++)
                Sptk.double_array_setitem(tmp, j, ar[i][j]);
            Sptk.double_p_array_setitem(res, i, tmp);
        }

        return res;
    }
}
