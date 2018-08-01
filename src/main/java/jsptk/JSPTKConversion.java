package jsptk;

/**
 *
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class JSPTKConversion
{
    private static double[] powerSpectrum(double[] spectrum) {
        int size = spectrum.length/2;
        double[] pow = new double[size];
        for (int k = 0; k < size; k++)
             pow[k] = spectrum[k] * spectrum[k] + spectrum[k+size] * spectrum[k+size];

         return pow;
    }

    private static double[][] powerSpectrum(double[][] spectrum) {
        int size = spectrum[0].length/2;
        double[][] pow = new double[spectrum.length][size];
        for (int t=0; t<spectrum.length; t++)
            for (int k = 0; k < size; k++)
                pow[t][k] = spectrum[t][k] * spectrum[t][k] + spectrum[t][k+size] * spectrum[t][k+size];

         return pow;
    }

    public static double[][] mc2sp(double[][] mc, double alpha, int fftlen) {
        double[][] c = JSPTKWrapper.freqt(mc, fftlen/2, -alpha);
        for (int t=0; t<c.length; t++)
            c[t][0] *= 2.0;

        // Compute symetry
        double[][] c_sym = new double[mc.length][fftlen];
        for (int t=0; t<c.length; t++) {
            for (int i=0; i<c[0].length; i++) {
                c_sym[t][i] = c[t][i];
                c_sym[t][c.length-1-i] = c[t][i];
            }
        }

        // back to power spectrum
        double[][] pow_spectrum = powerSpectrum(JSPTKWrapper.fftr(c_sym));

        for (int t=0; t<pow_spectrum.length; t++) {
            for (int i=0; i<pow_spectrum[0].length; i++) {
                pow_spectrum[t][i] = Math.exp(pow_spectrum[t][i]);
            }
        }
        return pow_spectrum;
    }

    public static double[] mc2sp(double[] mc, double alpha, int fftlen) {
        double[] c = JSPTKWrapper.freqt(mc, fftlen/2, -alpha);
        c[0] *= 2.0;

        // Compute symetry
        double[] c_sym = new double[fftlen];
        for (int i=0; i<c.length; i++) {
            c_sym[i] = c[i];
            c_sym[c.length-1-i] = c[i];
        }

        // back to power spectrum
        double[] pow_spectrum = powerSpectrum(JSPTKWrapper.fftr(c_sym));
        for (int i=0; i<pow_spectrum.length; i++) {
            pow_spectrum[i] = Math.exp(pow_spectrum[i]);
        }
        return pow_spectrum;
    }
}
