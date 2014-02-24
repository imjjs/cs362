package cs362.hw1;

import cs362.ClassificationLabel;
import cs362.Instance;
import cs362.Label;
import cs362.Predictor;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by yli on 1/15/14.
 */

public class MajorityClassifier extends Predictor implements Serializable{
    private String _most_common_label = null;

    public void train(List<Instance> instances){
        Map<String, Integer> count = new HashMap<String, Integer>(); //to count the Label
        for(Instance i : instances){
            String k = i.getLabel().toString();
            int v;
            if(count.containsKey(k) == true)
                v = count.get(k);
            else
                v = 0;
            count.put(k, ++v);
        }
        int max_value = Collections.max(count.values());
        for(Map.Entry<String, Integer> e : count.entrySet()){
            if(e.getValue() == max_value){
                this._most_common_label = e.getKey();
            }

        }
    }

    public Label predict(Instance instance){
        return new ClassificationLabel(Integer.parseInt(_most_common_label));
    }
}
