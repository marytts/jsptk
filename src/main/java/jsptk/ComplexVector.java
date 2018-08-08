package jsptk;

/**
 *  A class to represent a complex vector structure.
 *
 *  A complex vector is composed by a first array representing a real part and a second one
 *  representing the imaginary one
 */
public class ComplexVector {
    /** The real part array */
    public double[] real;

    /** The imaginary par array */
    public double[] im;

    /**
     *  The constructor
     *
     *  @param real the real part
     *  @param im the imaginary part
     */
    public ComplexVector(double[] real, double[] im) {
        this.real = real;
        this.im = im;
    }
}
