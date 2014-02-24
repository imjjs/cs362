package cs362.hw3;

import org.apache.commons.math3.linear.RealMatrix;

import java.io.Serializable;

/**
 * Created by yli on 2/15/14.
 */
public class GaussianKernelLogisticRegression extends KernelLogisticRegression implements Serializable {
    double sigma;
    public GaussianKernelLogisticRegression(double _eta, int _iterator_time, double _sigma){
        super(_eta,_iterator_time);
        sigma = _sigma;
    }

    protected double kernelFunc(RealMatrix x_i, RealMatrix x_j){
        RealMatrix tmp = x_i.add(x_j.scalarMultiply(-1));
        double numerator = tmp.multiply(tmp.transpose()).getEntry(0,0);
        return java.lang.Math.exp(-numerator / (2 * sigma * sigma));
    }
}
