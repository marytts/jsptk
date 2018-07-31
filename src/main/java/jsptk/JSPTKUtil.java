package jsptk;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 *
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class JSPTKUtil
{
    public static double mcepalpha(int fs) {
        return mcepalpha(fs, 0.0, 1.0, 0.001, 1000);
    }

    public static double mcepalpha(int fs, double start, double stop, double step, int num_points) {
        double alpha = 0.0;
        DoubleStream alpha_candidates =  IntStream.rangeClosed(0, (int)((stop-start)/step)).mapToDouble(x -> x*step + start);
        // double[] mel_scale = melscale_vector(fs, num_points);
        // double[] warp_vector = warping_vector(alpha, num_points);

        return alpha;
    }

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

// def mcepalpha(fs, start=0.0, stop=1.0, step=0.001, num_points=1000):
//     """Compute appropriate frequency warping parameter given a sampling frequency
//     It would be useful to determine alpha parameter in mel-cepstrum analysis.
//     The code is traslated from https://bitbucket.org/happyalu/mcep_alpha_calc.
//     Parameters
//     ----------
//     fs : int
//         Sampling frequency
//     start : float
//         start value that will be passed to numpy.arange. Default is 0.0.
//     stop : float
//         stop value that will be passed to numpy.arange. Default is 1.0.
//     step : float
//         step value that will be passed to numpy.arange. Default is 0.001.
//     num_points : int
//         Number of points used in approximating mel-scale vectors in fixed-
//         length.
//     Returns
//     -------
//     alpha : float
//         frequency warping paramter (offen denoted by alpha)
//     See Also
//     --------
//     pysptk.sptk.mcep
//     pysptk.sptk.mgcep
//     """
//     alpha_candidates = np.arange(start, stop, step)
//     mel = _melscale_vector(fs, num_points)
//     distances = [rms_distance(mel, _warping_vector(alpha, num_points)) for
//                  alpha in alpha_candidates]
//     return alpha_candidates[np.argmin(distances)]


// def _melscale_vector(fs, length):
//     step = (fs / 2.0) / length
//     melscalev = 1000.0 / np.log(2) * np.log(1 + step * np.arange(0, length) / 1000.0)
//     return melscalev / melscalev[-1]


// def _warping_vector(alpha, length):
//     step = np.pi / length
//     omega = step * np.arange(0, length)
//     num = (1 - alpha * alpha) * np.sin(omega)
//     den = (1 + alpha * alpha) * np.cos(omega) - 2 * alpha
//     warpfreq = np.arctan(num / den)
//     warpfreq[warpfreq < 0] += np.pi
//     return warpfreq / warpfreq[-1]


// def rms_distance(v1, v2):
//     d = v1 - v2
//     return np.sum(np.abs(d * d)) / len(v1)
}
