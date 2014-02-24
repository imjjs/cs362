package cs362.hw3;

import org.apache.commons.math3.linear.RealMatrix;

import java.io.Serializable;

/**
 * Created by yli on 2/15/14.
 */
public class PolynomialKernelLogisticRegression extends KernelLogisticRegression implements Serializable {
    double degree;
    public PolynomialKernelLogisticRegression(double _eta, int _interator_time, double _degree){
        super(_eta,_interator_time);
        degree = _degree;
    }

    protected double kernelFunc(RealMatrix x_i, RealMatrix x_j){
        return java.lang.Math.pow(1+ x_i.multiply(x_j.transpose()).getEntry(0,0),degree);
    }
}
