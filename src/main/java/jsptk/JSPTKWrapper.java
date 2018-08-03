package jsptk;

import java.io.IOException;

import cz.adamh.utils.NativeUtils;
import jsptk.Sptk;
/**
 *
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class JSPTKWrapper
{
    /* Loading library part */
    static {
        String libResourceName;
        String osName = System.getProperty("os.name");
        switch (osName) {
            case ("Mac OS X"):
                libResourceName = "libsptk.dylib";
                break;
            case ("Linux"):
                libResourceName = "libsptk.so";
                break;
            default:
                throw new RuntimeException("Cannot load library for OS: " + osName);
        }
        try {
            NativeUtils.loadLibraryFromJar("/" + libResourceName);
        } catch (IOException e) {
            e.printStackTrace(); // This is probably not the best way to handle exception :-)
        }
    }

    /**********************************************************************
     *** Vector operations
     **********************************************************************/
    public static double[] c2acr(double[] c, int order, int fftlen) {
        double[][] c_em = {c};
        return c2acr(c_em, order, fftlen)[0];

    }

    public static double[] freqt(double[] c, int order, double alpha) {
        double[][] c_em = {c};
        return freqt(c_em, order, alpha)[0];
    }

    public static double[] mc2b(double[] mc, double alpha) {
        double[][] mc_em = {mc};
        return mc2b(mc_em, alpha)[0];
    }


    public static double[] b2mc(double[] b, double alpha) {
        double[][] b_em = {b};
        return b2mc(b_em, alpha)[0];
    }


    public static ComplexVector fftr(double[] c) {
        double[][] c_em = { c };

        return fftr(c_em)[0];
    }

    /**********************************************************************
     *** Vector operations
     **********************************************************************/
    public static double[][] c2acr(double[][] c, int order, int fftlen) {
        SWIGTYPE_p_double c_sp = Sptk.new_double_array(c[0].length);
        SWIGTYPE_p_double r_sp = Sptk.new_double_array(order);
        double[][] r = new double[c.length][order];

        // Convert c to
        for (int t=0; t<c.length; t++) {
            copy(c[t], c_sp);
            Sptk.c2acr(c_sp, c.length, r_sp, order, fftlen);
            r[t] = JSPTKWrapper.swig2java(r_sp, order);
        }

        JSPTKWrapper.clean(c_sp);
        JSPTKWrapper.clean(r_sp);

        return r;

    }

    public static double[][] freqt(double[][] c, int order, double alpha) {
        // Wrap and prepare memory
        SWIGTYPE_p_double c_sp = Sptk.new_double_array(c[0].length);
        SWIGTYPE_p_double c2_sp = Sptk.new_double_array(order+1);
        double[][] c2 = new double[c.length][order+1];

        // Apply SPTK freqt
        for (int t=0; t<c.length; t++) {
            copy(c[t], c_sp);
            Sptk.freqt(c_sp, c[0].length, c2_sp, order, alpha);
            c2[t] = JSPTKWrapper.swig2java(c2_sp, order+1);
        }

        //  Clean memory
        JSPTKWrapper.clean(c_sp);
        JSPTKWrapper.clean(c2_sp);

        return c2;
    }



    public static double[][] mc2b(double[][] mc, double alpha) {
        SWIGTYPE_p_double mc_sp = Sptk.new_double_array(mc[0].length);
        SWIGTYPE_p_double b_sp = Sptk.new_double_array(mc[0].length);
        double[][] b = new double[mc.length][mc[0].length];

        // Convert c to
        for (int t=0; t<mc.length; t++) {
            copy(mc[t], mc_sp);
            Sptk.mc2b(mc_sp, b_sp, mc.length, alpha);
            b[t] = JSPTKWrapper.swig2java(b_sp, mc[t].length);
        }

        JSPTKWrapper.clean(mc_sp);
        JSPTKWrapper.clean(b_sp);

        return b;
    }


    public static double[][] b2mc(double[][] b, double alpha) {
        SWIGTYPE_p_double b_sp = Sptk.new_double_array(b[0].length);
        SWIGTYPE_p_double mc_sp = Sptk.new_double_array(b[0].length);
        double[][] mc = new double[b.length][b[0].length];

        // Convert c to
        for (int t=0; t<b.length; t++) {
            copy(b[t], b_sp);
            Sptk.mc2b(b_sp, mc_sp, b.length, alpha);
            mc[t] = JSPTKWrapper.swig2java(mc_sp, b[t].length);
        }

        JSPTKWrapper.clean(b_sp);
        JSPTKWrapper.clean(mc_sp);

        return mc;
    }

    public static ComplexVector[] fftr(double[][] c) {

        // Allocating memory
        SWIGTYPE_p_double c_sp = Sptk.new_double_array(c[0].length);
        SWIGTYPE_p_double sp_sp = Sptk.new_double_array(c[0].length);

        ComplexVector[] sp = new ComplexVector[c.length];

        // Convert c to
        for (int t=0; t<c.length; t++) {
            copy(c[t], c_sp);
            Sptk.fftr(c_sp, sp_sp, c[t].length);
            sp[t] = new ComplexVector(JSPTKWrapper.swig2java(c_sp, c[t].length),
                                      JSPTKWrapper.swig2java(sp_sp, c[t].length));
        }

        // Clean memory
        JSPTKWrapper.clean(sp_sp);
        JSPTKWrapper.clean(c_sp);

        return sp;
    }

    /**********************************************************************
     *** JNI Utilities
     **********************************************************************/
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

    private static void copy(double[] ar, SWIGTYPE_p_double dest) {
        for (int i=0; i<ar.length; i++)
            Sptk.double_array_setitem(dest, i, ar[i]);
    }

    private static void clean(SWIGTYPE_p_double ar) {
        Sptk.delete_double_array(ar);
    }
}
