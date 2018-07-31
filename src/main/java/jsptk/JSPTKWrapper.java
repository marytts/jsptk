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

    /**********************************************************************
     *** Vector operations
     **********************************************************************/
    public double[] c2acr(double[] c, int order, int fftlen) {
        SWIGTYPE_p_double c_sp = JSPTKWrapper.java2swig(c);
        SWIGTYPE_p_double r_sp = Sptk.new_double_array(order);

        // Convert c to
        Sptk.c2acr(c_sp, c.length, r_sp, order, fftlen);

        double[] r = JSPTKWrapper.swig2java(r_sp, order);
        JSPTKWrapper.clean(c_sp);
        JSPTKWrapper.clean(r_sp);

        return r;

    }

    public double[] freqt(double[] c, int order, double alpha) {
        SWIGTYPE_p_double c_sp = JSPTKWrapper.java2swig(c);
        SWIGTYPE_p_double c2_sp = Sptk.new_double_array(order);

        // Convert c to
        Sptk.freqt(c_sp, c.length, c2_sp, order, alpha);

        double[] c2 = JSPTKWrapper.swig2java(c_sp, order);
        JSPTKWrapper.clean(c_sp);
        JSPTKWrapper.clean(c2_sp);

        return c2;
    }



    public double[] mc2b(double[] mc, double alpha) {
        SWIGTYPE_p_double mc_sp = JSPTKWrapper.java2swig(mc);
        SWIGTYPE_p_double b_sp = Sptk.new_double_array(mc.length);

        // Convert c to
        Sptk.mc2b(mc_sp, b_sp, mc.length, alpha);
        double[] b = JSPTKWrapper.swig2java(b_sp, mc.length);

        JSPTKWrapper.clean(mc_sp);
        JSPTKWrapper.clean(b_sp);

        return b;
    }


    public double[] b2mc(double[] b, double alpha) {
        SWIGTYPE_p_double b_sp = JSPTKWrapper.java2swig(b);
        SWIGTYPE_p_double mc_sp = Sptk.new_double_array(b.length);

        // Convert c to
        Sptk.b2mc(b_sp, mc_sp, b.length, alpha);
        double[] mc = JSPTKWrapper.swig2java(mc_sp, b.length);
        JSPTKWrapper.clean(mc_sp);
        JSPTKWrapper.clean(b_sp);

        return mc;
    }



    /**********************************************************************
     *** Vector operations
     **********************************************************************/
    public double[][] c2acr(double[][] c, int order, int fftlen) {
        SWIGTYPE_p_p_double c_sp = JSPTKWrapper.java2swig(c);
        SWIGTYPE_p_double r_sp = Sptk.new_double_array(order);
        double[][] r = new double[c.length][order];

        // Convert c to
        for (int t=0; t<c.length; t++) {
            Sptk.c2acr(Sptk.double_p_array_getitem(c_sp, t), c.length, r_sp, order, fftlen);
            r[t] = JSPTKWrapper.swig2java(r_sp, order);
        }

        JSPTKWrapper.clean(c_sp, c.length);
        JSPTKWrapper.clean(r_sp);

        return r;

    }

    public double[][] freqt(double[][] c, int order, double alpha) {
        SWIGTYPE_p_p_double c_sp = JSPTKWrapper.java2swig(c);
        SWIGTYPE_p_double c2_sp = Sptk.new_double_array(order);
        double[][] c2 = new double[c.length][order];

        // Convert c to
        for (int t=0; t<c.length; t++) {
            Sptk.freqt(Sptk.double_p_array_getitem(c_sp, t), c.length, c2_sp, order, alpha);
            c2[t] = JSPTKWrapper.swig2java(c2_sp, order);
        }
        JSPTKWrapper.clean(c_sp, c.length);
        JSPTKWrapper.clean(c2_sp);

        return c2;
    }



    public double[][] mc2b(double[][] mc, double alpha) {
        SWIGTYPE_p_p_double mc_sp = JSPTKWrapper.java2swig(mc);
        SWIGTYPE_p_double b_sp = Sptk.new_double_array(mc[0].length);
        double[][] b = new double[mc.length][mc[0].length];

        // Convert c to
        for (int t=0; t<mc.length; t++) {
            Sptk.mc2b(Sptk.double_p_array_getitem(mc_sp, t), b_sp, mc.length, alpha);
            b[t] = JSPTKWrapper.swig2java(b_sp, mc[t].length);
        }

        JSPTKWrapper.clean(mc_sp, mc.length);
        JSPTKWrapper.clean(b_sp);

        return b;
    }


    public double[][] b2mc(double[][] b, double alpha) {
        SWIGTYPE_p_p_double b_sp = JSPTKWrapper.java2swig(b);
        SWIGTYPE_p_double mc_sp = Sptk.new_double_array(b[0].length);
        double[][] mc = new double[b.length][b[0].length];

        // Convert c to
        for (int t=0; t<b.length; t++) {
            Sptk.mc2b(Sptk.double_p_array_getitem(b_sp, t), mc_sp, b.length, alpha);
            mc[t] = JSPTKWrapper.swig2java(mc_sp, b[t].length);
        }

        JSPTKWrapper.clean(b_sp, b.length);
        JSPTKWrapper.clean(mc_sp);

        return mc;
    }


    /**********************************************************************
     *** JNI Utilities
     **********************************************************************/
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




    private static void clean(SWIGTYPE_p_p_double ar, int length) {
        for (int t=0; t<length; t++)
            Sptk.delete_double_array(Sptk.double_p_array_getitem(ar, t));
        Sptk.delete_double_p_array(ar);
    }

    private static void clean(SWIGTYPE_p_double ar) {
        Sptk.delete_double_array(ar);
    }
}
