package jsptk;

/**
 *  Class which provides conversion helpers
 *
 *  @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class JSPTKConversion
{

    /**
     *  Method to convert MFCC vector to spectrum vector
     *
     *  @param mc the MFCC vector
     *  @param alpha the all pass constant
     *  @param fftlen the length of the FFT
     *  @return the spectrum
     */
    public static double[] mc2sp(double[] mc, double alpha, int fftlen) {
        double[][] mc_em = { mc };
        return mc2sp(mc_em, alpha, fftlen)[0];
    }

    /**
     *  Method to convert MFCC matrix (result of a framed data) to spectrum matrix
     *
     *  @param mc the MFCC vector
     *  @param alpha the all pass constant
     *  @param fftlen the length of the FFT
     *  @return the spectrum
     */
    public static double[][] mc2sp(double[][] mc, double alpha, int fftlen) {
        double[][] c = JSPTKWrapper.freqt(mc, fftlen/2, -alpha);
        for (int t=0; t<c.length; t++)
            c[t][0] *= 2.0;

        // Compute symetry
        double[][] c_sym = new double[mc.length][fftlen];
        for (int t=0; t<c.length; t++) {
            c_sym[t][0] = c[t][0];
            for (int i=1; i<c[0].length; i++) {
                c_sym[t][i] = c[t][i];
                c_sym[t][c_sym[t].length-i] = c[t][i];
            }
        }

        // back to power spectrum
        ComplexVector[] sp = JSPTKWrapper.fftr(c_sym);
        double[][] pow_spectrum = new double[mc.length][sp[0].real.length/2+1];

        for (int t=0; t<pow_spectrum.length; t++) {
            for (int i=0; i<pow_spectrum[t].length; i++) { // FIXME: /2 is to be consistent with python code
                pow_spectrum[t][i] = Math.exp(sp[t].real[i]);
            }
        }

        return pow_spectrum;
    }
}
