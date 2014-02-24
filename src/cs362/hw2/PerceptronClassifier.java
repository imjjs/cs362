package cs362.hw2;

import cs362.ClassificationLabel;
import cs362.Instance;
import cs362.Label;
import cs362.Predictor;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by yli on 1/28/14.
 */
public class PerceptronClassifier extends Predictor implements Serializable {
    double online_learning_rate;
    int online_training_iterations;
    RealMatrix small_w;
    TreeSet<Integer> avaible_feature = new TreeSet<Integer>();
    int row;
    private int getRow(List<Instance> instances){return instances.size();}
    private void verify_feature(List<Instance> instances){
        for(Instance i : instances){
            for(Map.Entry<Integer, Double> e : i.getFeatureVector().getEntrySet()){
                avaible_feature.add(e.getKey());
            }
        }
    }

    public PerceptronClassifier(double _online_learning_rate, int _online_training_iterations){
        this.online_learning_rate = _online_learning_rate;
        this.online_training_iterations = _online_training_iterations;
    }

    private void predict_when_train(Instance i){
        int _label = Integer.parseInt(i.getLabel().toString());
        int _p_label;
        Map<Integer, Double> feature = i.getFeatureVector().getMap();
        RealMatrix small_x_i = new Array2DRowRealMatrix(row, 1);
        for(int j = 0; j < row; ++j){
            Double value = feature.get(j + 1);
            if(null == value)
                value = 0d;
            small_x_i.addToEntry(j, 0, value);
        }
        double product = small_w.transpose().multiply(small_x_i).getEntry(0, 0);
        _p_label = (product > 0)? 1 : 0;
        if(_p_label != _label){
            _label = (1 == _label)?1:-1;
            small_w = small_x_i.scalarMultiply(_label*online_learning_rate).add(small_w);
        }


    }

    public void train(List<Instance> instances){
        verify_feature(instances);
        row = avaible_feature.last();
        small_w = new Array2DRowRealMatrix(row, 1);
        for(int p = 0; p < online_training_iterations; ++p){
            for(Instance i : instances){
                predict_when_train(i);
            }
        }
    }

    public Label predict(Instance instance){
        RealMatrix small_x_i = new Array2DRowRealMatrix(row, 1);
        Map<Integer, Double> feature = instance.getFeatureVector().getMap();
        for(int i = 0; i < row; ++i){
            Double value = feature.get(i + 1);
            if(null == value)
                value = 0d;
            small_x_i.addToEntry(i, 0, value);
        }
        double product = small_w.transpose().multiply(small_x_i).getEntry(0, 0);
        if(product > 0)
            return new ClassificationLabel(1);
        else
            return new ClassificationLabel(0);
    }
}
