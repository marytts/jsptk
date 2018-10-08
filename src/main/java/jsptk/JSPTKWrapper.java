package jsptk;

import java.io.IOException;

import cz.adamh.utils.NativeUtils;
import jsptk.Sptk;

/**
 *  The convinient wrapper class around JNI sptk class.
 *
 *  The javadoc indicated function called + adaptation. However, for more information about the
 *  actual function, please read the SPTK documentation.
 *
 *  @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
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
    /**
     *  The method to call c2acr on a vector of double
     *
     *  @param c the vector
     *  @param order the needed order
     *  @param fftlen the length of the fft
     *  @return the autocorrelation vector of double
     */
    public static double[] c2acr(double[] c, int order, int fftlen) {
        double[][] c_em = {c};
        return c2acr(c_em, order, fftlen)[0];

    }

    /**
     *  The method to call freqt on a vector of double
     *
     *  @param c the vector
     *  @param order the needed order
     *  @param alpha the all pass constant
     *  @return the frequency transformed vector of double
     */
    public static double[] freqt(double[] c, int order, double alpha) {
        double[][] c_em = {c};
        return freqt(c_em, order, alpha)[0];
    }

    /**
     *  The method to call mc2b on a vector of double
     *
     *  @param mc the MCC vector
     *  @param alpha the all pass constant
     *  @return the MLSA digital filter coefficients double vector
     */
    public static double[] mc2b(double[] mc, double alpha) {
        double[][] mc_em = {mc};
        return mc2b(mc_em, alpha)[0];
    }

    /**
     *  The method to call b2mc on a vector of double
     *
     *  @param b the MLSA digital filter coefficients
     *  @param alpha the all pass constant
     *  @return the MCC
     */
    public static double[] b2mc(double[] b, double alpha) {
        double[][] b_em = {b};
        return b2mc(b_em, alpha)[0];
    }

    /**
     *  The method to call mcep on a vector of double
     *
     *  @param x the input sequence as a vector
     *  @param order order of the mel cepstrum
     *  @param alpha frequency warping coefficient
     *  @param itr1 minimum number of iteration
     *  @param itr2 maximum number of iteration
     *  @param dd end condition
     *  @param etype type of use for parameter e (0: not used, 1: initial value for the log-periodogram, 2: floor periodogram in dB)
     *  @param e initial value or floor (see etype) for periodogram
     *  @param f mimimum value of the determinant of the normal matrix
     *  @param itype; the input type format
     *  @return the vector of mel cepstrum coefficients
     */
    public static double[] mcep(double[] x,  int order,
                                  double alpha, int itr1, int itr2,
                                  double dd, int etype, double e,
                                  double f, int itype) throws Exception {
        double[][] x_em = {x};
        return mcep(x_em, order, alpha, itr1, itr2, dd, etype, e, f, itype)[0];
    }

    /**
     *  The method to call fftr on a vector of double
     *
     *  @param c the double vector of real coefficients
     *  @return a complex vector corresponding to the FFT results
     */
    public static ComplexVector fftr(double[] c) {
        double[][] c_em = { c };

        return fftr(c_em)[0];
    }

    /**********************************************************************
     *** Matrix operations
     **********************************************************************/
    /**
     *  The method to call c2acr on a matrix of double, result of a framed data
     *
     *  @param c the matrix
     *  @param order the needed order
     *  @param fftlen the length of the fft
     *  @return the autocorrelation matrix of double
     */
    public static double[][] c2acr(double[][] c, int order, int fftlen) {
        SWIGTYPE_p_double c_sp = Sptk.new_double_array(c[0].length);
        SWIGTYPE_p_double r_sp = Sptk.new_double_array(order+1);
        double[][] r = new double[c.length][order];

        // Convert c to
        for (int t=0; t<c.length; t++) {
            copy(c[t], c_sp);
            Sptk.c2acr(c_sp, c.length, r_sp, order, fftlen);
            r[t] = JSPTKWrapper.swig2java(r_sp, order+1);
        }

        JSPTKWrapper.clean(c_sp);
        JSPTKWrapper.clean(r_sp);

        return r;

    }

    /**
     *  The method to call freqt on a matrix of double, result of a framed data
     *
     *  @param c the matrix
     *  @param order the needed order
     *  @param alpha the all pass constant
     *  @return the frequency transformed matrix of double
     */
    public static double[][] freqt(double[][] c, int order, double alpha) {
        // Wrap and prepare memory
        SWIGTYPE_p_double c_sp = Sptk.new_double_array(c[0].length);
        SWIGTYPE_p_double c2_sp = Sptk.new_double_array(order+1);
        double[][] c2 = new double[c.length][order+1];

        // Apply SPTK freqt
        for (int t=0; t<c.length; t++) {
            copy(c[t], c_sp);
            Sptk.freqt(c_sp, c[0].length-1, c2_sp, order, alpha);
            c2[t] = JSPTKWrapper.swig2java(c2_sp, order+1);
        }

        //  Clean memory
        JSPTKWrapper.clean(c_sp);
        JSPTKWrapper.clean(c2_sp);

        return c2;
    }

    /**
     *  The method to call mc2b on a matrix of double, result of a framed data
     *
     *  @param mc the MCC matrix
     *  @param alpha the all pass constant
     *  @return the MLSA digital filter coefficients double matrix
     */
    public static double[][] mc2b(double[][] mc, double alpha) {
        SWIGTYPE_p_double mc_sp = Sptk.new_double_array(mc[0].length);
        SWIGTYPE_p_double b_sp = Sptk.new_double_array(mc[0].length);
        double[][] b = new double[mc.length][mc[0].length];

        // Convert c to
        for (int t=0; t<mc.length; t++) {
            copy(mc[t], mc_sp);
            Sptk.mc2b(mc_sp, b_sp, mc[t].length-1, alpha);
            b[t] = JSPTKWrapper.swig2java(b_sp, mc[t].length);
        }

        JSPTKWrapper.clean(mc_sp);
        JSPTKWrapper.clean(b_sp);

        return b;
    }

    /**
     *  The method to call b2mc on a matrix of double, result of a framed data
     *
     *  @param b the MLSA digital filter coefficients
     *  @param alpha the all pass constant
     *  @return the MCC
     */
    public static double[][] b2mc(double[][] b, double alpha) {
        SWIGTYPE_p_double b_sp = Sptk.new_double_array(b[0].length);
        SWIGTYPE_p_double mc_sp = Sptk.new_double_array(b[0].length);
        double[][] mc = new double[b.length][b[0].length];

        // Convert c to
        for (int t=0; t<b.length; t++) {
            copy(b[t], b_sp);
            Sptk.b2mc(b_sp, mc_sp, b[t].length-1, alpha);
            mc[t] = JSPTKWrapper.swig2java(mc_sp, b[t].length);
        }

        JSPTKWrapper.clean(b_sp);
        JSPTKWrapper.clean(mc_sp);

        return mc;
    }

    /**
     *  Call mcep on a matrix
     *
     *  @param x the input sequence as a matrix
     *  @param orderl order of the mel cepstrum
     *  @param alpha frequency warping coefficient
     *  @param itr1 minimum number of iteration
     *  @param itr2 maximum number of iteration
     *  @param dd end condition
     *  @param etype type of use for parameter e (0: not used, 1: initial value for the log-periodogram, 2: floor periodogram in dB)
     *  @param e initial value or floor (see etype) for periodogram
     *  @param f mimimum value of the determinant of the normal matrix
     *  @param itype; the input type format
     *  @return the matrix of mel cepstrum coefficients per frame
     */
    public static double[][] mcep(double[][] x, int order,
                                  double alpha, int itr1, int itr2,
                                  double dd, int etype, double e,
                                  double f, int itype) throws Exception {

        // Prepare swig conversion
        SWIGTYPE_p_double x_sp = Sptk.new_double_array(x[0].length);
        SWIGTYPE_p_double mc_sp = Sptk.new_double_array(x[0].length);
        double[][] mc = new double[x.length][order+1];

        // Generate mel-ceptrum for each frame
        for (int t=0; t<x.length; t++) {
            copy(x[t], x_sp);
            int flng = x[t].length;
            if (itype != 0)
                flng = (x[t].length-1) * 2;

            int ret_val = Sptk.mcep(x_sp, flng, mc_sp, order, alpha, itr1, itr2, dd, etype, e, f, itype);

            // Check that there is no problem
            switch (ret_val) {
            case 1:
                throw new Exception("invalid etype has been given: " + etype);
            case 2:
                throw new Exception("invalid itype has been given: " + itype);
            case 3:
                throw new Exception("failed to compute mel-cepstrum");
            case 4:
                throw new Exception("zero(s) are found in the periodogram");

            }

            mc[t] = JSPTKWrapper.swig2java(mc_sp, order+1);
        }

        JSPTKWrapper.clean(x_sp);
        JSPTKWrapper.clean(mc_sp);

        return mc;
    }

    /**
     *  The method to call fftr on a matrix of double, result of a framed data
     *
     *  @param c the double matrix of real coefficients
     *  @return a complex matrix corresponding to the FFT results
     */
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
    /**
     *  Util method to convert a swig array to a native double array in java
     *
     *  This method doesn't clean any memory !
     *
     *  @param ar the swig array
     *  @param length the length of the array
     *  @return the java native double array containing the values from the swig array
     */
    private static double[] swig2java(SWIGTYPE_p_double ar, int length) {
        double[] res = new double[length];

        for (int i=0; i<length; i++)
            res[i] = Sptk.double_array_getitem(ar, i);

        return res;
    }

    /**
     *  Utilitary method to generate a swig array from a java native double array
     *
     *  @param ar the double array
     *  @return the swig array containing the values from the java native double array
     */
    private static SWIGTYPE_p_double java2swig(double[] ar) {
        SWIGTYPE_p_double res = Sptk.new_double_array(ar.length);

        for (int i=0; i<ar.length; i++)
            Sptk.double_array_setitem(res, i, ar[i]);

        return res;
    }

    /**
     *  Method to copy the containing of the java native array into a preallocated swig array
     *
     *  @param src the java native array
     *  @param dest the preallocated swig array
     */
    private static void copy(double[] src, SWIGTYPE_p_double dest) {
        for (int i=0; i<src.length; i++)
            Sptk.double_array_setitem(dest, i, src[i]);
    }

    /**
     *  Method to clear the memory of a swig double array
     *
     *  @param ar the swig array to free
     */
    private static void clean(SWIGTYPE_p_double ar) {
        Sptk.delete_double_array(ar);
    }
}
