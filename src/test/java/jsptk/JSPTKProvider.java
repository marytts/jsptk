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

    public static double[] providerB() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.b");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);
        double[] b = new double[lines.size()];
        for (int i = 0; i < b.length; i++) {
            b[i] = Double.parseDouble(lines.get(i));
        }

        return b;
    }

    public static double[] providerMGC() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.mgc");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);
        double[] mgc = new double[lines.size()];
        for (int i = 0; i < mgc.length; i++) {
            mgc[i] = Double.parseDouble(lines.get(i));
        }

        return mgc;
    }


    // FIXME: hardcoded nb of frames
    public static double[][] providerMGCFull() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.mgc_full");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);
        int dim = lines.size()/341;

        double[][] mgc = new double[341][dim];
        for (int i = 0; i<mgc.length; i++) {
            for (int j=0; j<dim; j++)
                mgc[i][j] = Double.parseDouble(lines.get(i*dim+j));
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

    public static double[] providerMCEP() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.mcep");
        List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);

        double[] mcep = new double[lines.size()];
        for (int i = 0; i < mcep.length; i++) {
            mcep[i] = Double.parseDouble(lines.get(i));
        }

        return mcep;
    }

    public static double[] providerPostFilteredMGC() throws Exception {
        URL url = JSPTKWrapperTest.class.getResource("/test.postfilt");
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
