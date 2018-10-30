package jsptk;

/**
 *  Class which provides static utility methods
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class JSPTKUtil
{
    /**
     *  Compute the most accurate alpha value needed for mel-cepstral analysis based on the sample
     *  rate
     *
     *  @param fs the sample rate
     *  @return the alpha value
     */
    public static double getMCEPAlpha(int fs) {
        return getMCEPAlpha(fs, 0.0, 1.0, 0.001, 1000);
    }

    /**
     *  Compute the most accurate alpha value needed for mel-cepstral analysis based on the sample
     *  rate. Other parameters are there to tune the distance computation
     *
     *  @param fs the sample rate
     *  @param start the start value
     *  @param stop the stop value
     *  @param the step to divide the interval between start and stop
     *  @param num_points the number of points used for the mel scale vector computation
     *  @return the alpha value
     */
    public static double getMCEPAlpha(int fs, double start, double stop, double step, int num_points) {
        double alpha = 0.0;
        double[] mel_scale = generateMelScaleVector(fs, num_points);
        double max = Double.POSITIVE_INFINITY;
        for (double cur_alpha=start; cur_alpha<stop; cur_alpha+=step) {
            double[] warp_vector = generateWarpingVector(cur_alpha, num_points);
            double d = computeRMS(mel_scale, warp_vector);
            if (max > d) {
                alpha = cur_alpha;
                max = d;
            }
        }

        return alpha;
    }

    /**
     *  Method to generate the mel scale vector based on the sample rate
     *
     *  @param fs the sample rate
     *  @param the length of the produced vector
     *  @return the mel scale vector
     */
    public static double[] generateMelScaleVector(int fs, int length) {
        double step = (fs/2.0) / length;
        double fact = 1000.0 / Math.log(2);

        double[] scale = new double[length];
        double den = 0;
        for (int i=length-1; i>=0; i--) {
            scale[i] = fact * Math.log(1+step*i/1000.0);
            if (den == 0)
                den = scale[i];
            scale[i] = scale[i] / den;
        }

        return scale;
    }

    /**
     *  Method to generate the warping vector based on the sample rate
     *
     *  @param fs the sample rate
     *  @param the length of the produced vector
     *  @return the warping vector
     */
    public static double[] generateWarpingVector(double alpha, int length) {
        double step = Math.PI / length;
        double omega, num, den, global_den = 0;
        double[] warpfreq = new double[length];

        for (int i=length-1; i>=0; i--) {
            omega = step * i;
            num = (1 - alpha*alpha) * Math.sin(omega);
            den = (1 + alpha*alpha) * Math.cos(omega) - 2*alpha;
            warpfreq[i] = Math.atan(num / den);
            if (warpfreq[i] < 0) {
                warpfreq[i] += Math.PI;
            }

            if (global_den == 0)
                global_den = warpfreq[i];

            warpfreq[i] = warpfreq[i] / global_den;
        }

        return warpfreq;
    }

    /**
     *  Method to compute the RMS distance between 2 vectors
     *
     *  @param ar1 a vector
     *  @param ar2 a vector
     *  @return the rms distance
     */
    public static double computeRMS(double[] ar1, double[] ar2) {

        double rms = 0.0;
        for (int i=0; i<ar1.length; i++) {
            double d = ar1[i]-ar2[i];
            rms += d*d/ar1.length;
        }

        return rms;
    }

}
