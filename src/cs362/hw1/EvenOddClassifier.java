package cs362.hw1;

import cs362.ClassificationLabel;
import cs362.Instance;
import cs362.Label;
import cs362.Predictor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yli on 1/15/14.
 */
public class EvenOddClassifier extends Predictor implements Serializable {
    private boolean isEven(int testNum){
        return (testNum%2 == 0);
    }
    public void train(List<Instance> instances){
        ;
    }

    public Label predict(Instance inst){
        double odd_sum = 0;
        double even_sum = 0;
        Set<Map.Entry<Integer, Double>> vec = inst.getFeatureVector().getEntrySet();
        for(Map.Entry<Integer, Double> e : vec){
            if(isEven(e.getKey()))
                even_sum += e.getValue();
            else
                odd_sum += e.getValue();
        }

        if(even_sum >= odd_sum)
            return new ClassificationLabel(1);
        else
            return new ClassificationLabel(0);

    }


}
