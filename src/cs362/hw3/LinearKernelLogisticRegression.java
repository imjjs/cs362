package cs362.hw3;

import cs362.Predictor;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.Serializable;

/**
 * Created by yli on 2/15/14.
 */
public class LinearKernelLogisticRegression extends KernelLogisticRegression implements Serializable {

    public LinearKernelLogisticRegression(double _eta, int _iterator_time){
        super(_eta,_iterator_time);
    }

    protected double kernelFunc(RealMatrix x_i, RealMatrix x_j){
        return x_i.multiply(x_j.transpose()).getEntry(0,0);
    }
}
