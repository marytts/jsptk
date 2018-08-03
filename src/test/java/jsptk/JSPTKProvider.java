package jsptk;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.io.Resources;

/**
 *
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">Sébastien Le Maguer</a>
 */
public class JSPTKProvider
{

    public static double[] providerMGC() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.mgc");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);
        double[] mgc = new double[lines.size()];
        for (int i = 0; i < mgc.length; i++) {
            mgc[i] = Double.parseDouble(lines.get(i));
        }

        return mgc;
    }

    public static double[] providerFREQT() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.freqt");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);

        double[] freqt = new double[lines.size()];
        for (int i = 0; i < freqt.length; i++) {
            freqt[i] = Double.parseDouble(lines.get(i));
        }

        return freqt;
    }

    public static double[] providerSP() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.sp");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);

        double[] sp = new double[lines.size()];
        for (int i = 0; i < sp.length; i++) {
            sp[i] = Double.parseDouble(lines.get(i));
        }

        return sp;
    }



    public static double[] providerMC2SP() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.mc2sp");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);

        double[] sp = new double[lines.size()];
        for (int i = 0; i < sp.length; i++) {
            sp[i] = Double.parseDouble(lines.get(i));
        }

        return sp;
    }
}
