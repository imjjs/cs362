package cs362.hw2;

import cs362.ClassificationLabel;
import cs362.Instance;
import cs362.Label;
import cs362.Predictor;

import java.io.Serializable;
import java.util.*;

/**
 * Created by yli on 1/27/14.
 */
public class NaiveBayesClassifier extends Predictor implements Serializable {
    public NaiveBayesClassifier(double _lambda){
        this.lambda = _lambda;
    }
    double lambda;
    double p_0;
    double p_1;
    int numberof_0;
    int numberof_1;
    int sizeof_sample;
    Map<Integer, Double> p_y_when_c0 = new HashMap<Integer, Double>();
    Map<Integer, Double> p_y_when_c1 = new HashMap<Integer, Double>();
    Set<Integer> avaible_feature = new TreeSet<Integer>();
    private void p_c(List<Instance> instances){
        int count = 0;
        for(Instance i : instances){
            if(0 == Integer.parseInt(i.getLabel().toString()))
                ++count;
        }

        sizeof_sample = instances.size();
        numberof_0 = count;
        numberof_1 = sizeof_sample - numberof_0;
        p_0 = 1d * numberof_0 / sizeof_sample;
        p_1 = 1d - p_0;
    }

    private void verify_feature(List<Instance> instances){
        for(Instance i : instances){
            for(Map.Entry<Integer, Double> e : i.getFeatureVector().getEntrySet()){
                avaible_feature.add(e.getKey());
            }
        }
    }
    private Double get_p_y_i_when_c(int index, int valueof_c, List<Instance> instances){
        int count = 0;
        for(Instance i : instances){
            if(valueof_c != Integer.parseInt(i.getLabel().toString()))
                continue;
            Map<Integer, Double> tmp = i.getFeatureVector().getMap();
            Double value = tmp.get(index);
            if(null == value)
                value = 0d;
            if(value >= 0.5)
                ++count;
        }
//        if(1 == valueof_c)
//            System.out.println(count);
        if (0 == valueof_c)
            return 1d * (count + lambda) / (numberof_0 + lambda);
        else
            return 1d * (count + lambda) / (numberof_1 + lambda);
    }

    public void train(List<Instance> instances){
        p_c(instances);
        verify_feature(instances);
        for(Integer i : avaible_feature){
            //System.out.print(i.toString() + ':');
            p_y_when_c0.put(i, get_p_y_i_when_c(i, 0, instances));
            p_y_when_c1.put(i, get_p_y_i_when_c(i, 1, instances));
        }


           // System.out.println(p_y_when_c0.get(i));


    }
    private double predt_0(Instance instance){
        Map<Integer, Double> feature = instance.getFeatureVector().getMap();
        double log_count = 0d;
        for(Integer i : avaible_feature){
            Double value = feature.get(i);
            if(null == value)
                value = 0d;
            if(value >= 0.5)
                log_count += java.lang.Math.log(p_y_when_c0.get(i));
        }
        return log_count + java.lang.Math.log(p_0);
    }

    private double predt_1(Instance instance){
        Map<Integer, Double> feature = instance.getFeatureVector().getMap();
        double log_count = 0d;
        for(Integer i : avaible_feature){
            Double value = feature.get(i);
            if(null == value)
                value = 0d;
            if(value >= 0.5)
                log_count += java.lang.Math.log(p_y_when_c1.get(i));
        }
        return log_count + java.lang.Math.log(p_1);
    }

    public Label predict(Instance instance){
        Double c_0 = predt_0(instance);
        Double c_1 = predt_1(instance);
        if(c_0 > c_1)
            return new ClassificationLabel(0);
        else
            return new ClassificationLabel(1);
    }
}
