package jsptk;

// IO
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.IOException;

// Audio
import javax.sound.sampled.AudioInputStream;

// Native library loading
import cz.adamh.utils.NativeUtils;

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
     *** Signal preparation
     **********************************************************************/
    public static double[] extractRAWFromStream(AudioInputStream ais) throws IOException {

        // Get the stream to a byte buffer
        byte[] data = new byte[ais.available()];
        ais.read(data);
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        // Now extract raw values (short => double)
        double[] x = new double[data.length / Short.BYTES];
        for (int i=0; i<x.length; i++) {
            x[i] = buf.getShort();
        }

        return x;
    }
    /**
     *  Framing method.
     *
     *  @param x the signal to frame
     *  @param frame_length the length of the frame
     *  @param frame_shift the shift between 2 frames in the signal
     *  @param not_centered deactivated for now
     *  @return the framed signal
     *  FIXME: default is centered for now !
     */
    public static double[][] frame(double[] x, int frame_length, int frame_shift, boolean not_centered) {
        int nb_frames = (x.length + frame_shift - 1) / frame_shift;
        double[][] framed_data = new double[nb_frames][frame_length];

        for (int f=0; f<nb_frames; f++) {
            int start = f*frame_shift - frame_length / 2;
            for (int l=0; l<frame_length; l++) {
                int i = start + l;
                if ((i<0) || (i>=x.length)) {
                    framed_data[f][l] = 0;
                } else {
                    framed_data[f][l] = x[i];
                }
            }
        }
        return framed_data;
    }


    /**
     *  Windowing method
     *
     *  @param framed_signal the framed signal
     *  @param new_frame_length the output frame length
     *  @param norm_type the normalisation type
     *  @param win_type the window type
     *  @return the windowed data
     */
    public static double[][] window(double[][] framed_signal, int new_frame_length, int norm_type, Window win_type) {
        SWIGTYPE_p_double x_sp = Sptk.new_double_array(new_frame_length);
        double[][] windowed_signal = new double[framed_signal.length][framed_signal[0].length];

        // Convert c to
        for (int t=0; t<framed_signal.length; t++) {
            copy(framed_signal[t], x_sp);
            Sptk.window(win_type, x_sp, framed_signal[t].length, norm_type);
            windowed_signal[t] = JSPTKWrapper.swig2java(x_sp, new_frame_length);
        }

        JSPTKWrapper.clean(x_sp);

        return windowed_signal;
    }


    /**********************************************************************
     *** Pitch/F0
     **********************************************************************/

    /**
     *  Method to extract pitch from raw signal.
     *
     *  For now only RAPT is supported
     *
     *  @param x the raw signal
     *  @param frame_shift the frame shift (in points)
     *  @param sampling_rate the sampling rate in Hz
     *  @param atype (0: RAPT, 1: swipe (not supported yet))
     *  @param otype (0: pitch, 1: f0, 2: log(f0))
     *  @param lower_f0 lower f0 threshold
     *  @param upper_f0 upper f0 threshold
     *  @param voice_threshold the voicing decision thresold (optimal value depends on the method used to extract the F0, see SPTK documentation)
     *  @return the pitch values
     */
    public static double[] pitch(double[] x, int frame_shift, int sampling_rate,
                                 int atype, int otype,
                                 double lower_f0, double upper_f0, double voice_threshold) {
        int nb_frames = (x.length + frame_shift - 1) / frame_shift;
        SWIGTYPE_p_float x_sp = JSPTKWrapper.java2swigFloat(x);
        SWIGTYPE_p_float f0_sp = Sptk.new_float_array(nb_frames);

        if (atype == 0) {
            Sptk.rapt(x_sp, f0_sp,
                      x.length, sampling_rate, frame_shift,
                      lower_f0, upper_f0, voice_threshold, otype);
        } else {
            throw new IllegalArgumentException("Swipe is not implemented yet");
            // Sptk.swipe(x_sp, x.length, sampling_rate, frame_shift, lower_f0, upper_f0,
            //           voice_threshold, otype);
        }

        double[] f0 = JSPTKWrapper.swig2java(f0_sp, nb_frames);

        JSPTKWrapper.clean(x_sp);
        JSPTKWrapper.clean(f0_sp);

        return f0;
    }

    /**********************************************************************
     *** Spectrum/Cesptrum vector
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
     *** Spectrum/Cesptrum matrix
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
     *  Call mgcep on a matrix coming from a windowed signal and using some default parameters
     *
     *  @param x the signal windowed
     *  @param order the order of the mel-generalized cepstrum
     *  @param alpha the alpha value
     *  @param per_err small value added to the periodogram
     *  @return mgcep(x, order, alpha, 0, 0, 0, 0, 2, 30, 0.001, -1, per_err, 0, 0.000001);
     */
    public static double[][] mgcepDefaultWav(double[][] x, int order, double alpha, double per_err) throws Exception {
        return mgcep(x, order, alpha, 0, 0, 0, 0, 2, 30, 0.001, -1, per_err, 0, 0.000001);
    }

    /**
     *  Call mgcep on a matrix coming from a windowed signal.
     *
     *  @param x the signal windowed
     *  @param order the order of the mel-generalized cepstrum
     *  @param alpha the alpha value
     *  @param gamma the gamma value
     *  @param c FIXME?
     *  @param in_format the input format
     *  @param out_format the output format
     *  @param it_min the minimum iteration
     *  @param it_max the maximum iteration
     *  @param end_cond the end condition
     *  @param rec_order the order of the recursions
     *  @param per_err small value added to the periodogram
     *  @param floor floor in db calculated per frame (FIXME: not used for now)
     *  @param min_det minimum value of the determinant
     *  @return the matrix of mel cepstrum coefficients per frame
     */
    public static double[][] mgcep(double[][] x,int order, double alpha, double gamma,
                                   int c, int in_format, int out_format, int it_min, int it_max,
                                   double end_cond, int rec_order, double per_err, double floor, double min_det)
        throws Exception {

        int etype = 1; // FIXME: check that
        // Some variable adaptation
        if (rec_order == -1) {
            rec_order = x[0].length - 1;
        }

        int ilng = x[0].length;
        if (in_format != 0) {
            ilng = ilng/2 + 1;
        }

        // Prepare Memory
        SWIGTYPE_p_double x_sp = Sptk.new_double_array(x[0].length);
        SWIGTYPE_p_double b_sp = Sptk.new_double_array(2*(order+1));
        double[][] mc = new double[x.length][order+1];

        // Generate mel-ceptrum for each frame
        for (int t=0; t<x.length; t++) {
            // copy memory
            copy(x[t], x_sp);

            // Call mgcep
            Sptk.mgcep(x_sp, x[t].length, b_sp, order, alpha, gamma,
                       rec_order, it_min, it_max, end_cond,
                       etype, per_err, min_det, in_format);

            // Adapt output
            if  ((out_format == 0) || (out_format == 1) || (out_format == 2) ||  (out_format == 4)) {
                Sptk.ignorm(b_sp, b_sp, order, gamma);
            }

            if  ((out_format == 0) || (out_format == 2) || (out_format == 4)) {
                if (alpha != 0.0)
                    Sptk.b2mc(b_sp, b_sp, order, alpha);
            }

            if  ((out_format == 2) || (out_format == 4)) {
                Sptk.gnorm(b_sp, b_sp, order, gamma);
            }

            // Transform array back to java
            mc[t] = JSPTKWrapper.swig2java(b_sp, order+1);

            // Final adaptation
            if  ((out_format == 4) || (out_format == 5)) {
                for (int i=order; i>0; i--)
                    mc[t][i] *= gamma;
            }

        }

        JSPTKWrapper.clean(x_sp);
        JSPTKWrapper.clean(b_sp);

        return mc;
    }

    /**
     *  Method to convert MGC vector to spectrum vector
     *
     *  @param mgc the MGC vector
     *  @param alpha the all pass constant
     *  @param gamma the gamma value
     *  @param c ????
     *  @param normalized_input input as normalized cepstrum
     *  @param multiplied_gamma_input input as multiplied by gamma
     *  @param fftlen the length of the FFT
     *  @param output_type the output type
     *  @param output_phase flag to indicate is phase instead of amplitude should be outputed
     *  @return the spectrum
     */
    public static double[][] mgc2sp(double[][] mgc, double alpha, double gamma, int c,
                                    boolean normalized_input, boolean multiplied_gamma_input,
                                    int fftlen, int output_type, boolean output_phase) throws Exception {
        // Prepare some constants

        int no = fftlen / 2 + 1;
        double logk = 20.0 / Math.log(10.0);

        // Prepare SWIG memory
        double[][] sp = new double[mgc.length][no];
        SWIGTYPE_p_double mgc_sp = Sptk.new_double_array(mgc[0].length);
        SWIGTYPE_p_double x_sp = Sptk.new_double_array(fftlen);
        SWIGTYPE_p_double y_sp = Sptk.new_double_array(fftlen);

        for (int t=0; t<mgc.length; t++) {

            // Prepare input
            if (normalized_input) {
                copy(mgc[t], mgc_sp);
                Sptk.ignorm(mgc_sp, mgc_sp, mgc[t].length-1, gamma);
                mgc[t] = swig2java(mgc_sp, mgc[t].length);
            } else if (multiplied_gamma_input) {
                if (gamma == 0) {
                    throw new Exception("gamma for input mgc coefficients should not equal to 0 if " +
                                        " multiplied_gamma_input parameter is set to true");
                }
                mgc[t][0] = (mgc[t][0] - 1.0) / gamma;
            }

            if (multiplied_gamma_input) {
                if (gamma == 0) {
                    throw new Exception("gamma for input mgc coefficients should not equal to 0 if " +
                                        " multiplied_gamma_input parameter is set to true");
                }
                for (int i = mgc[t].length-1; i > 0; i--)
                    mgc[t][i] /= gamma;
            }

            // Apply mgc2sp
            copy(mgc[t], mgc_sp);
            Sptk.mgc2sp(mgc_sp, mgc[t].length-1, alpha, gamma, x_sp, y_sp, fftlen);

            // Generate output
            double[] x = swig2java(x_sp, no);
            double[] y = swig2java(y_sp, no);
            if (output_phase) {
                switch(output_type) {
                case 1:
                    for (int i=no-1; i>=0; i--)
                        sp[t][i] = y[i];
                    break;
                case 2:
                    for (int i=no-1; i>=0; i--)
                        sp[t][i] = y[i] * 180 / Math.PI;
                    break;
                default:
                    for (int i=no-1; i>=0; i--)
                        sp[t][i] = y[i] / Math.PI;
                }
            } else {
                switch(output_type) {
                case 1:
                    break;
                case 2:
                    for (int i=no-1; i>=0; i--)
                        sp[t][i] = Math.exp(x[i]);
                    break;
                case 3:
                    for (int i=no-1; i>=0; i--)
                        sp[t][i] = Math.exp(2 * x[i]);
                    break;
                default:
                    for (int i=no-1; i>=0; i--)
                        sp[t][i] = x[i] * logk;
                }
            }
        }

        // Clean memory
        JSPTKWrapper.clean(mgc_sp);
        JSPTKWrapper.clean(x_sp);
        JSPTKWrapper.clean(y_sp);

        // return the final spectrum
        return sp;
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
     *  Util method to convert a swig array of float to a native double array in java
     *
     *  This method doesn't clean any memory !
     *
     *  @param ar the swig array of float values
     *  @param length the length of the array
     *  @return the java native double array containing the values from the swig array
     */
    private static double[] swig2java(SWIGTYPE_p_float ar, int length) {
        double[] res = new double[length];

        for (int i=0; i<length; i++)
            res[i] = Sptk.float_array_getitem(ar, i);

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
     *  Utilitary method to generate a swig float array from a java native double array
     *
     *  @param ar the double array
     *  @return the swig float array containing the values from the java native double array
     */
    private static SWIGTYPE_p_float java2swigFloat(double[] ar) {
        SWIGTYPE_p_float res = Sptk.new_float_array(ar.length);

        for (int i=0; i<ar.length; i++)
            Sptk.float_array_setitem(res, i, (float) ar[i]);

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

    /**
     *  Method to clear the memory of a swig double array
     *
     *  @param ar the swig array to free
     */
    private static void clean(SWIGTYPE_p_float ar) {
        Sptk.delete_float_array(ar);
    }
}
