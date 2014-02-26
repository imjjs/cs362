package cs362.hw4;

import cs362.Instance;
import cs362.Label;
import cs362.Predictor;

import java.util.*;

/**
 * Created by yli on 14-2-25.
 */
public class LambdaMeansPredictor extends Predictor {

    double lambda = 1.0;
    List<Instance> _instances;
    int row;
    int col;
    List<Integer> indicator = new ArrayList<Integer>();
    TreeSet<Integer> avaible_feature = new TreeSet<Integer>();
    List<Map<Integer, Double>> mu = new ArrayList<Map<Integer, Double>>() ;

    private void verify_feature(){

        for(Instance i : _instances){
            for(Map.Entry<Integer, Double> e : i.getFeatureVector().getEntrySet()){
                avaible_feature.add(e.getKey());
            }
        }
    }

    private int getCol(){
        return avaible_feature.last();
    }
    private int getRow(){
        return _instances.size();
    }

    private double getDistance(Map<Integer, Double> x1, Map<Integer, Double> x2){
        double result = 0d;
        for(int i: avaible_feature){
            double x1_i;
            double x2_i;
            Double tmp = x1.get(i);
            if(null == tmp)
                x1_i = 0d;
            else
                x1_i = tmp;
            tmp = x2.get(i);
            if(null == tmp)
                x2_i = 0d;
            else
                x2_i = tmp;
            tmp = x2_i - x1_i;
            result += tmp * tmp;
        }
        return result;
    }

    private Map<Integer, Double> getCenter(List<Map<Integer, Double>> feature_list){
        Map<Integer, Double> result = new TreeMap<Integer, Double>();
        int num = feature_list.size();
        for(int i : avaible_feature){
            double x_i_bar = 0;
            for(Map<Integer, Double> f : feature_list.){
                Double tmp = f.get(i);
                if(null == tmp)
                    tmp = 0d;
                x_i_bar += tmp;
            }
            result.put(i, x_i_bar/num);
        }
        return result;
    }

    private int e_step_i(Map<Integer, Double> features){
        int min = 0;
        double min_value = getDistance(features, mu.get(min));
        for(int i = 1; i < mu.size(); ++i){
            double tmp = getDistance(features, mu.get(i));
            if(tmp < min_value){
                min_value = tmp;
                min = i;
            }
        }
        if(min_value > lambda){
            mu.add(features);
            min = mu.size() - 1;
        }
        return min;
    }
    private void e_step(){
        for(int i = 0; i < _instances.size(); ++i){
            indicator.add(i,e_step_i(_instances.get(i).getFeatureVector().getMap()));
        }
    }

    private void init(List<Instance> instances){
        _instances = instances;
        verify_feature();
        row = getRow();
        col = getCol();

    }
    @Override
    public void train(List<Instance> instances) {
        init(instances);


    }

    @Override
    public Label predict(Instance instance) {
        return null;
    }
}
