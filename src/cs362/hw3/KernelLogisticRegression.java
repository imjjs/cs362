package cs362.hw3;

import cs362.ClassificationLabel;
import cs362.Instance;
import cs362.Label;
import cs362.Predictor;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by yli on 2/15/14.
 */
public abstract class KernelLogisticRegression extends Predictor implements Serializable {

    int row;
    int col = 10;
    double eta;
    RealMatrix gram;
    RealMatrix kappa;
    int iterator_time;
    RealMatrix alpha;
    List<Instance> inst;

    public KernelLogisticRegression(double _eta, int _iterator_time){
        eta = _eta;
        iterator_time = _iterator_time;
    }

    protected int getRow(){
        return inst.size();
    }

    protected abstract double kernelFunc(RealMatrix x_i, RealMatrix x_j);


    protected RealMatrix from_instance_to_vector(Instance _i){
        RealMatrix result = new Array2DRowRealMatrix(1,col);
        for(int j = 0; j < col; ++j){
            Map<Integer, Double> _m = _i.getFeatureVector().getMap();
            Double val = _m.get(j + 1);
            if(null == val)
                val = 0d;
            result.addToEntry(0, j, val);
        }
        return result;
    }

    protected RealMatrix get_ith_row(int i){
        Instance tmp = inst.get(i);
        return from_instance_to_vector(tmp);
    }

    protected double logic(double i){
        return (1d/(1 + java.lang.Math.exp(-i)));
    }
    protected int get_y_i(int i){
        Instance tmp = inst.get(i);
        return Integer.parseInt(tmp.getLabel().toString());
    }

    protected RealMatrix gradian(){
        RealMatrix result = new Array2DRowRealMatrix(1,row);
        for(int k = 0; k < row; ++k){
            double val = 0;
            for(int i = 0; i < row; ++i){
                if(1 == get_y_i(i))
                    val += gram.getEntry(i, k) * logic(-kappa.getEntry(0,i));
                else
                    val -= gram.getEntry(i, k) * logic(kappa.getEntry(0,i));
            }
            result.addToEntry(0, k, val);
        }
        return result;
    }

    protected void cacheKappa(){
        for(int i = 0;i < row; ++i){
            double val = 0;
            for(int j = 0; j < row; ++j){
                val +=alpha.getEntry(0,j) * gram.getEntry(j, i);
            }
            kappa.addToEntry(0, i, val);
        }
    }

    protected void cacheGram(){
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < row; ++j){
                gram.addToEntry(i, j, kernelFunc(get_ith_row(i), get_ith_row(j)));
            }
        }
    }


    public void train(List<Instance> instances){
        inst = instances;
        row = getRow();
        alpha = new Array2DRowRealMatrix(1,row);
        gram = new Array2DRowRealMatrix(row,row);
        kappa = new Array2DRowRealMatrix(1, row);
        cacheGram();
        for(int i = 0; i < iterator_time; ++i){
            cacheKappa();
            RealMatrix g = gradian();
            alpha = alpha.add(g.scalarMultiply(eta));
        }

    }

    public Label predict(Instance instance){
        RealMatrix x_i = from_instance_to_vector(instance);
        double sum = 0;
        for(int j = 0; j < row; ++j){
            sum += alpha.getEntry(0, j) * kernelFunc(get_ith_row(j), x_i);
        }
        double val = logic(sum);

        if(val >= 0.5)
            return new ClassificationLabel(1);
        else
            return new ClassificationLabel(0);

    }




}
