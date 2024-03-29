package cs362.hw4;

import cs362.ClassificationLabel;
import cs362.Instance;
import cs362.Label;
import cs362.Predictor;
import java.util.*;

/**
 * Created by yli on 14-2-25.
 */
public class LambdaMeansPredictor extends Predictor {

    double lambda;
    int iter_time;
    public LambdaMeansPredictor(double _lambda, int _iter_time){
        lambda = _lambda;
        iter_time =_iter_time;
    }
    List<Instance> _instances;
    int row;
    int col;
    List<Integer> indicator;
    TreeSet<Integer> avaible_feature = new TreeSet<Integer>();
    List<Map<Integer, Double>> mu = new ArrayList<Map<Integer,Double>>(200);
    List<Map<Integer, Double>> _mu;
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
        TreeSet<Integer> feature = new TreeSet<Integer>();
        for(Map.Entry<Integer, Double> e : x1.entrySet())
            feature.add(e.getKey());
        for(Map.Entry<Integer, Double> e : x2.entrySet())
            feature.add(e.getKey());
        for(int i: feature){
            Double x1_i;
            Double x2_i;
            x1_i = x1.get(i);
            if(null == x1_i)
                x1_i = 0d;
            x2_i = x2.get(i);
            if(null == x2_i)
                x2_i = 0d;
            double tmp = x2_i - x1_i;
            result += tmp * tmp;
        }
        return result;
    }

    private Map<Integer, Double> getCenter(List<Map<Integer, Double>> feature_list){
        Map<Integer, Double> result = new TreeMap<Integer, Double>();
        int num = feature_list.size();
        for(int i : avaible_feature){
            double x_i_bar = 0;
            for(Map<Integer, Double> f : feature_list){
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
            _mu.add(features);
            min = _mu.size() - 1;
        }
        return min;
    }
    private void e_step(){
        _mu = new ArrayList<Map<Integer, Double>>(mu);
        for(int i = 0; i < _instances.size(); ++i){

            indicator.set(i,e_step_i(_instances.get(i).getFeatureVector().getMap()));
        }
        mu = _mu;
    }

    private void m_step(){
        for(int i = 0; i < mu.size(); ++i){
            List<Map<Integer, Double>> tmp = new ArrayList<Map<Integer, Double>>();
            for(int j = 0; j < indicator.size(); ++j){
                if(indicator.get(j) == i)
                    tmp.add(_instances.get(j).getFeatureVector().getMap());
            }
            mu.set(i, getCenter(tmp));
        }
    }
    private void init(List<Instance> instances){
        _instances = instances;
        verify_feature();
        row = getRow();
        col = getCol();
        indicator = new ArrayList<Integer>(2000);
        List<Map<Integer, Double>> init_group = new ArrayList<Map<Integer, Double>>();
        for(int i = 0; i < row; ++i){
            indicator.add(0);
            init_group.add(_instances.get(i).getFeatureVector().getMap());
        }
        mu.add(getCenter(init_group));
        if(0.0 == lambda){
            double sum = 0d;
            for(int i = 0; i < row; ++i){
                sum += getDistance(mu.get(0), instances.get(i).getFeatureVector().getMap());
            }
            lambda = sum / row;
        }

    }
    @Override
    public void train(List<Instance> instances) {
        init(instances);
        for(int i = 0; i < iter_time; ++i){
            System.out.println(i);
            e_step();
            System.out.printf("m,%d\n", mu.size());
            m_step();
        }
        //the code below is to check those empty clutters set
        for(int i = 0; i < mu.size(); ++i){
            int count = 0;
            for(int j : indicator){
                if(j == i)
                    count++;
            }
            if (0 == count)
                mu.set(i, new TreeMap<Integer, Double>());
        }
    }

    @Override
    public Label predict(Instance instance) {
        Map<Integer, Double> feature = instance.getFeatureVector().getMap();
        int min = 0;
        double min_value = getDistance(feature, mu.get(min));
        for(int i = 1; i < mu.size(); ++i){
            double tmp = getDistance(feature, mu.get(i));
            if(tmp < min_value){
                min_value = tmp;
                min = i;
            }
        }
        return new ClassificationLabel(min);
    }
}
